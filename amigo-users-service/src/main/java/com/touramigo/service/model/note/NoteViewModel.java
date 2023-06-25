package com.touramigo.service.model.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.touramigo.service.entity.note.Note;
import com.touramigo.service.model.AbstractAuditingModel;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id", callSuper = false)
public class NoteViewModel extends AbstractAuditingModel {
    private UUID id;
    private UUID supplierId;
    private UUID partnerId;
    private UUID contactId;
    private UUID ruleId;
    private String content;

    public static NoteViewModel create(Note note) {
        NoteViewModel noteModel = new NoteViewModel();
        if (Objects.nonNull(note.getId())) {
            noteModel.setId(note.getId());
        }
        if (Objects.nonNull(note.getSupplier())) {
            noteModel.setSupplierId(note.getSupplier().getId());
        }
        if (Objects.nonNull(note.getContact())) {
            noteModel.setContactId(note.getContact().getId());
        }
        if (Objects.nonNull(note.getPartner())) {
            noteModel.setPartnerId(note.getPartner().getId());
        }
        if (Objects.nonNull(note.getRuleId())) {
            noteModel.setRuleId(note.getRuleId());
        }
        if (Objects.nonNull(note.getContent())) {
            noteModel.setContent(note.getContent());
        }
        return noteModel;
    }
}
