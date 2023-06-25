package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.PartnerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerProductRepository extends JpaRepository<PartnerProduct, UUID> {
    Optional<PartnerProduct> findById(UUID id);

    @Query("SELECT pp FROM PartnerProduct pp WHERE pp.partner.id = :partnerId")
    List<PartnerProduct> findByPartnerId(@Param("partnerId") UUID partnerId);
}
