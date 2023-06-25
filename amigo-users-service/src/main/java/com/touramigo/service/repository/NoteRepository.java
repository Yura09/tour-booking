package com.touramigo.service.repository;

import com.touramigo.service.entity.note.Note;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    Note findById(UUID id);

    List<Note> findByIdIn(Iterable<UUID> ids);

    @Query("select distinct n from Note n where n.supplier.id=:supplierId and n.createdBy in(select author.id from User author where author.partner.id=:partnerId) AND n.deleted = false ")
    List<Note> findAllByPartnerAndSupplier(@Param("partnerId") UUID partnerId, @Param("supplierId") UUID supplierId);

    @Query("select distinct n from Note n where n.contact.id=:contactId and n.createdBy in(select author.id from User author where author.partner.id=:partnerId) AND n.deleted = false ")
    List<Note> findAllByPartnerAndContact(@Param("partnerId") UUID partnerId, @Param("contactId") UUID contactId);

    @Query("select distinct n from Note n where n.ruleId=:ruleId and n.createdBy in(select author.id from User author where author.partner.id=:partnerId) AND n.deleted = false ")
    List<Note> findAllByPartnerAndRule(@Param("partnerId") UUID partnerId, @Param("ruleId") UUID ruleId);

    @Query("select distinct n from Note n where n.partner.id=:id and n.createdBy in(select author.id from User author where author.partner.id=:partnerId) AND n.deleted = false ")
    List<Note> findAllByPartner(@Param("partnerId") UUID partnerId, @Param("id") UUID id);
}
