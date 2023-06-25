package com.touramigo.service.service.impl;

import com.touramigo.service.entity.contact.Contact;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.model.ContactModel;
import com.touramigo.service.repository.ContactRepository;
import com.touramigo.service.repository.SupplierRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.service.ContactService;
import com.touramigo.service.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final SupplierRepository supplierRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ContactModel> findAllBySupplierId(UUID supplierId) {
        List<UUID> userIdsFromCurrentPartner = userRepository.findByPartnerId(SecurityUtil.getPartnerId()).map(User::getId)
            .collect(Collectors.toList());

        return contactRepository.findAllByPartnerAndSupplier(SecurityUtil.getPartnerId(), supplierId)
            .stream().peek(sup -> sup.setNotes(sup.getNotes()
                .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
                .collect(Collectors.toSet())))
            .map(ContactModel::create)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContactModel save(ContactModel contactRequest) {
        Supplier supplier = getSupplier(contactRequest.getSupplierId());
        Contact contact = this.iniContactVariables(contactRequest, new Contact());
        contact.setSupplier(supplier);
        contactRepository.save(contact);
        return ContactModel.create(contact);
    }

    @Override
    @Transactional
    public void deleteById(UUID contactId) {
        Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.CONTACT_NOT_FOUND));
        contactRepository.delete(contact);
    }

    @Override
    @Transactional
    public ContactModel update(UUID contactId, ContactModel contactModel) {
        contactModel.setId(contactId);
        Contact contact = contactRepository.findById(contactModel.getId())
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.CONTACT_NOT_FOUND));
        this.iniContactVariables(contactModel, contact);
        contactRepository.save(contact);
        return ContactModel.create(contact);
    }

    private Supplier getSupplier(UUID supplierId) {
        return supplierRepository.findById(supplierId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));
    }

    private Contact iniContactVariables(ContactModel contactRequest, Contact contact) {
        if (contact.getCreatedBy() == null) {
            contact.setCreatedBy(SecurityUtil.getCurrentUserId());
        }
        contact.setName(contactRequest.getName());
        contact.setJobTitle(contactRequest.getJobTitle());
        contact.setEmail(contactRequest.getEmail());
        contact.setMobile(contactRequest.getMobile());
        contact.setTelephone(contactRequest.getTelephone());
        contact.setLinkedIn(contactRequest.getLinkedin());
        contact.setSocialMedia(contactRequest.getSocialMedia());
        return contact;
    }
}
