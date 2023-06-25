package com.touramigo.service.service.impl;

import com.touramigo.service.entity.note.Note;
import com.touramigo.service.enums.NoteType;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.mapper.AbstractAuditingEntityMapper;
import com.touramigo.service.model.note.NoteCreateModel;
import com.touramigo.service.model.note.NoteUpdateModel;
import com.touramigo.service.model.note.NoteViewModel;
import com.touramigo.service.repository.ContactRepository;
import com.touramigo.service.repository.NoteRepository;
import com.touramigo.service.repository.PartnerRepository;
import com.touramigo.service.repository.SupplierRepository;
import com.touramigo.service.security.AccessGuard;
import com.touramigo.service.service.NoteService;
import com.touramigo.service.service.client.RuleClient;
import com.touramigo.service.util.SecurityUtil;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final AbstractAuditingEntityMapper abstractAuditingEntityMapper;
    private final AccessGuard accessGuard;
    private final SupplierRepository supplierRepository;
    private final PartnerRepository partnerRepository;
    private final ContactRepository contactRepository;
    private final RuleClient ruleClient;

    @Override
    @Transactional(readOnly = true)
    public List<NoteViewModel> findAllBySupplierId(UUID supplierId) {
        return noteRepository.findAllByPartnerAndSupplier(SecurityUtil.getPartnerId(), supplierId)
                .stream().map(this::render).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteViewModel> findAllByContactId(UUID contactId) {
        return noteRepository.findAllByPartnerAndContact(SecurityUtil.getPartnerId(), contactId)
                .stream().map(this::render).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteViewModel> findAllByPartnerId(UUID partnerId) {
        return noteRepository.findAllByPartner(SecurityUtil.getPartnerId(), partnerId)
                .stream().map(this::render).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NoteViewModel getById(UUID id) {
        Note note = getEntityById(id);
        accessGuard.validateUserHasReadAccess(note);
        return render(note);
    }

    @Override
    public NoteViewModel create(NoteCreateModel model) {

        Note note = this.initVariables(model);

        return render(noteRepository.save(note));
    }

    private Note initVariables(NoteCreateModel model) {
        Note note = new Note();
        switch (model.getParentType()) {
            case SUPPLIER:
                note.setSupplier(supplierRepository.findById(model.getParentId()).orElseThrow(
                        () -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND)));
                break;
            case CONTACT:
                note.setContact(contactRepository.findById(model.getParentId()).orElseThrow(
                        () -> new UnexistedDataException(UserServiceErrorMessages.CONTACT_NOT_FOUND)));
                break;
            case PARTNER:
                note.setPartner(partnerRepository.findById(model.getParentId()).orElseThrow(
                        () -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND)));
                break;
            case RULE:
                note.setRuleId(ruleClient.getRuleById(SecurityUtil.extractToken(), model.getParentId()).getId());
                break;
            default:
                throw new InvalidDataException(UserServiceErrorMessages.NOTE_PARENT_TYPE_NOT_FOUND);
        }
        note.setContent(model.getContent());
        return note;
    }

    @Override
    public NoteViewModel update(UUID id, NoteUpdateModel model) {
        Note note = getEntityById(id);
        accessGuard.validateUserHasModifyAccess(note);

        note.setContent(model.getContent());

        return render(note);
    }

    @Override
    public void delete(UUID id) {
        Note note = getEntityById(id);
        accessGuard.validateUserHasModifyAccess(note);

        noteRepository.delete(note);
    }

    @Override
    public void delete(Collection<UUID> ids) {
        List<Note> notes = getEntitiesByIdIn(ids);

        for (Note note : notes) {
            accessGuard.validateUserHasModifyAccess(note);
        }

        noteRepository.delete(notes);
    }

    @Override
    public List<NoteViewModel> findAllByRuleId(UUID ruleId) {
        return noteRepository.findAllByPartnerAndRule(SecurityUtil.getPartnerId(), ruleId)
                .stream().map(this::render).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteViewModel> findAllNotes(String type, UUID id) {
        switch (NoteType.getByValue(type)) {
            case SUPPLIER:
                return findAllBySupplierId(id);
            case CONTACT:
                return findAllByContactId(id);
            case PARTNER:
                return findAllByPartnerId(id);
            case RULE:
                return findAllByRuleId(id);
            default:
                throw new InvalidDataException(UserServiceErrorMessages.NOTE_PARENT_TYPE_NOT_FOUND);
        }
    }

    private NoteViewModel render(Note note) {
        NoteViewModel noteModel = NoteViewModel.create(note);
        abstractAuditingEntityMapper.copyFieldsFromTo(note, noteModel);

        return noteModel;
    }

    private Note getEntityById(UUID id) {
        Note note = noteRepository.findById(id);
        if (Objects.isNull(note)) {
            throw new UnexistedDataException(UserServiceErrorMessages.NOTE_NOT_FOUND);
        }

        return note;
    }

    private List<Note> getEntitiesByIdIn(Collection<UUID> ids) {
        if (!(ids instanceof Set)) {
            ids = new HashSet<>(ids);
        }

        List<Note> notes = noteRepository.findByIdIn(ids);
        if (notes.size() != ids.size()) {
            throw new UnexistedDataException(UserServiceErrorMessages.NOTE_NOT_FOUND);
        }

        return notes;
    }
}
