package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.PartnerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerGroupRepository extends JpaRepository<PartnerGroup, UUID> {

    Optional<PartnerGroup> findById(UUID id);

}
