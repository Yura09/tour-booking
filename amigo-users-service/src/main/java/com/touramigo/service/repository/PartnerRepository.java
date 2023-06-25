package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.Partner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, UUID> {

    Optional<Partner> findById(UUID id);

    List<Partner> findByMasterTrue();

    List<Partner> findByPartnerGroupId(UUID id);

    @Query("select distinct p from Partner p left join fetch p.partnerGroup pg left join fetch p.suppliers s")
    List<Partner> findAllForCommercial();

}
