package com.touramigo.service.service.impl;

import com.touramigo.service.entity.supplier.TermsAndConditions;
import com.touramigo.service.entity.supplier.TermsAndConditionsState;
import com.touramigo.service.entity.supplier.TermsAndConditionsType;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.model.TermsAndConditionsCreateUpdateDto;
import com.touramigo.service.model.TermsAndConditionsViewDto;
import com.touramigo.service.repository.SupplierRepository;
import com.touramigo.service.repository.TermsAndConditionsRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsAndConditionsService {

    private final TermsAndConditionsRepository tacRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    @Transactional
    public TermsAndConditions create(UUID supplierId, TermsAndConditionsCreateUpdateDto tacDto) {
        List<TermsAndConditions> existingTac = tacRepository.findAllBySupplierId(supplierId);
        if (existingTac.stream().anyMatch(it -> it.getState().equals(TermsAndConditionsState.DRAFT))) {
            throw new DataIntegrityViolationException("Can not create new terms and conditions when there is an unpublished draft");
        }
        int version = existingTac.size() + 1;
        TermsAndConditions tac = new TermsAndConditions();
        tac.setId(UUID.randomUUID());
        tac.setState(tacDto.getType().equals(TermsAndConditionsType.MARKDOWN) ? TermsAndConditionsState.DRAFT : TermsAndConditionsState.PUBLISHED);
        tac.setVersion(version);
        tac.setCreatedBy(userRepository.findByEmail(SecurityUtil.getCurrentUser().getEmail()).get());
        tac.setCreatedAt(LocalDateTime.now());
        tac.setSupplier(supplierRepository.findOne(supplierId));
        tac.setDescription(tacDto.getDescription());
        tac.setType(tacDto.getType());
        tac.setContent(tacDto.getContent());

        return tacRepository.save(tac);
    }

    @Transactional
    public UUID update(TermsAndConditionsCreateUpdateDto dto) {
        TermsAndConditions tac = tacRepository.findById(dto.getId()).orElseThrow(() -> new UnexistedDataException("No terms and conditions found by provided id"));
        tac.setType(dto.getType());
        tac.setDescription(dto.getDescription());
        tac.setContent(dto.getContent());

        return tacRepository.save(tac).getId();
    }

    public void publish(UUID id) {
        TermsAndConditions tac = tacRepository.findById(id).orElseThrow(() -> new UnexistedDataException("No terms and conditions found by provided id"));
        if (tac.getState().equals(TermsAndConditionsState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Already published");
        }
        tac.setState(TermsAndConditionsState.PUBLISHED);
        tac.setPublishedAt(LocalDateTime.now());
        tac.setPublishedBy(userRepository.findByEmail(SecurityUtil.getCurrentUser().getEmail()).get());

        tacRepository.save(tac);
    }

    public List<TermsAndConditionsViewDto> getList(UUID supplierId) {
        List<TermsAndConditions> termsAndConditionsList = tacRepository.findAllBySupplierId(supplierId);
        return termsAndConditionsList.stream()
                .map(it -> TermsAndConditionsViewDto.builder()
                        .id(it.getId())
                        .supplierId(it.getSupplier().getId())
                        .supplierName(it.getSupplier().getName())
                        .createdBy(it.getCreatedBy().getFullName())
                        .createdAt(it.getCreatedAt())
                        .publishedBy(it.getPublishedBy() != null ? it.getPublishedBy().getFullName() : null)
                        .publishedAt(it.getPublishedAt())
                        .version(it.getVersion())
                        .state(it.getState())
                        .type(it.getType())
                        .build())
                .collect(Collectors.toList());
    }

    public TermsAndConditionsViewDto getOne(UUID tacId) {
        TermsAndConditions termsAndConditions = tacRepository.findById(tacId).orElseThrow(() -> new UnexistedDataException("Could not find terms and conditions"));
        return TermsAndConditionsViewDto.builder()
                .id(termsAndConditions.getId())
                .supplierId(termsAndConditions.getSupplier().getId())
                .supplierName(termsAndConditions.getSupplier().getName())
                .createdBy(termsAndConditions.getCreatedBy().getFullName())
                .createdAt(termsAndConditions.getCreatedAt())
                .publishedBy(termsAndConditions.getPublishedBy() != null ? termsAndConditions.getPublishedBy().getFullName() : null)
                .publishedAt(termsAndConditions.getPublishedAt())
                .version(termsAndConditions.getVersion())
                .state(termsAndConditions.getState())
                .type(termsAndConditions.getType())
                .description(termsAndConditions.getDescription())
                .content(termsAndConditions.getContent())
                .build();
    }

    public TermsAndConditionsViewDto getLatest(UUID supplierId) {
        List<TermsAndConditions> termsAndConditionsList = tacRepository.findAllBySupplierId(supplierId);
        TermsAndConditions termsAndConditions =  termsAndConditionsList.stream()
                .filter(it -> it.getState().equals(TermsAndConditionsState.PUBLISHED))
                .max(Comparator.comparingInt(TermsAndConditions::getVersion))
                .orElseThrow(() -> new UnexistedDataException("Supplier has no terms and conditions"));

        return TermsAndConditionsViewDto.builder()
                .id(termsAndConditions.getId())
                .supplierId(termsAndConditions.getSupplier().getId())
                .supplierName(termsAndConditions.getSupplier().getName())
                .createdBy(termsAndConditions.getCreatedBy().getFullName())
                .createdAt(termsAndConditions.getCreatedAt())
                .publishedBy(termsAndConditions.getPublishedBy() != null ? termsAndConditions.getPublishedBy().getFullName() : null)
                .publishedAt(termsAndConditions.getPublishedAt())
                .version(termsAndConditions.getVersion())
                .state(termsAndConditions.getState())
                .type(termsAndConditions.getType())
                .description(termsAndConditions.getDescription())
                .content(termsAndConditions.getContent())
                .build();
    }
}
