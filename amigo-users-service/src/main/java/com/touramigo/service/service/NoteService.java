package com.touramigo.service.service;

import com.touramigo.service.model.note.NoteCreateModel;
import com.touramigo.service.model.note.NoteUpdateModel;
import com.touramigo.service.model.note.NoteViewModel;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface NoteService {

    List<NoteViewModel> findAllBySupplierId(UUID supplierId);

    List<NoteViewModel> findAllByContactId(UUID contactId);

    List<NoteViewModel> findAllByPartnerId(UUID partnerId);

    NoteViewModel getById(UUID id);

    NoteViewModel create(NoteCreateModel model);

    NoteViewModel update(UUID id, NoteUpdateModel model);

    void delete(UUID id);

    void delete(Collection<UUID> ids);

    List<NoteViewModel> findAllByRuleId(UUID ruleId);

    List<NoteViewModel> findAllNotes(String type, UUID id);
}
