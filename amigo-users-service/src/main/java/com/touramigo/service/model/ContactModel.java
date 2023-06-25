package com.touramigo.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.touramigo.service.entity.contact.Contact;
import com.touramigo.service.entity.note.Note;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactModel {

    private UUID id;
    @NotEmpty
    private String name;

    private String jobTitle;

    private String email;

    private String mobile;

    private String telephone;

    private String linkedin;

    private String socialMedia;

    private UUID supplierId;

    private List<UUID> notes;

    public static ContactModel create(Contact contact) {
        ContactModel contactModel = new ContactModel();
        contactModel.setId(contact.getId());
        if (Objects.nonNull(contact.getName())) {
            contactModel.setName(contact.getName());
        }

        if (Objects.nonNull(contact.getJobTitle())) {
            contactModel.setJobTitle(contact.getJobTitle());
        }
        if (Objects.nonNull(contact.getEmail())) {
            contactModel.setEmail(contact.getEmail());
        }

        if (Objects.nonNull(contact.getMobile())) {
            contactModel.setMobile(contact.getMobile());
        }

        if (Objects.nonNull(contact.getTelephone())) {
            contactModel.setTelephone(contact.getTelephone());
        }

        if (Objects.nonNull(contact.getLinkedIn())) {
            contactModel.setLinkedin(contact.getLinkedIn());
        }

        if (Objects.nonNull(contact.getSocialMedia())) {
            contactModel.setSocialMedia(contact.getSocialMedia());
        }

        if (Objects.nonNull(contact.getSupplier())) {
            contactModel.setSupplierId(contact.getSupplier().getId());
        }

        if (Objects.nonNull(contact.getNotes())) {
            contactModel.setNotes(contact.getNotes().stream().map(Note::getId).collect(Collectors.toList()));
        }

        return contactModel;
    }
}
