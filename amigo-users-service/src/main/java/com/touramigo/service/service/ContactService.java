package com.touramigo.service.service;

import com.touramigo.service.model.ContactModel;
import java.util.List;
import java.util.UUID;

public interface ContactService {
    List<ContactModel> findAllBySupplierId(UUID supplierId);

    ContactModel save(ContactModel contactRequest);

    void deleteById(UUID contactId);

    ContactModel update(UUID contactId, ContactModel contactModel);
}
