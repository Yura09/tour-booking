package com.touramigo.service.service.impl;

import com.touramigo.service.entity.history.PartnerHistory;
import com.touramigo.service.mapper.PartnerHistoryMapper;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.model.history.PartnerHistoryViewModel;
import com.touramigo.service.repository.PartnerHistoryRepository;
import com.touramigo.service.repository.PartnerRepository;
import com.touramigo.service.service.PartnerHistoryService;
import com.touramigo.service.util.PartnerHistoryMapperUtil;
import com.touramigo.service.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnerHistoryServiceImpl implements PartnerHistoryService {

    private final PartnerHistoryRepository partnerHistoryRepository;
    private final PartnerHistoryMapper partnerHistoryMapper;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PartnerHistoryViewModel> findAllByPartner(UUID id) {
        return partnerHistoryRepository.findAllByPartnerIdOrderByDateLogDesc(id)
            .stream()
            .map(this::render)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(PartnerModel partnerModel) {
        PartnerHistory partnerHistory = new PartnerHistory();
        partnerHistory.setData(PartnerHistoryMapperUtil.getPartnerModel(partnerModel));
        partnerHistory.setPartner(partnerRepository.findOne(partnerModel.getId()));
        partnerHistory.setModifiedBy(SecurityUtil.getCurrentUserId());
        partnerHistoryRepository.save(partnerHistory);
    }

    private PartnerHistoryViewModel render(PartnerHistory history) {
        PartnerHistoryViewModel viewModel = PartnerHistoryViewModel.create(history);
        partnerHistoryMapper.copyFieldsFromTo(history, viewModel);

        return viewModel;
    }
}
