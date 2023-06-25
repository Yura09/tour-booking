package com.touramigo.service.repository;

import com.touramigo.service.entity.contact.Contact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    @Query("select distinct c from Contact c where c.supplier.id=:supplierId and c.createdBy in(select author.id from User author where author.partner.id=:partnerId) ")
    List<Contact> findAllByPartnerAndSupplier(@Param("partnerId") UUID partnerId, @Param("supplierId") UUID supplierId);

    Optional<Contact> findById(UUID contactId);
}
