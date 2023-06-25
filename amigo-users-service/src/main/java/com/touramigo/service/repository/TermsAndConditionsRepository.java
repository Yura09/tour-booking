package com.touramigo.service.repository;

import com.touramigo.service.entity.supplier.TermsAndConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermsAndConditionsRepository extends JpaRepository<TermsAndConditions, UUID> {

    Optional<TermsAndConditions> findById(UUID id);

    List<TermsAndConditions> findAllBySupplierId(UUID supplierId);

}
