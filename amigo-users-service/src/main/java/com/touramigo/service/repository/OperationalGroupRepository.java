package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.OperationalGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationalGroupRepository extends JpaRepository<OperationalGroup, UUID> {
    Optional<OperationalGroup> findById(UUID id);

    @Query("SELECT pp FROM OperationalGroup pp WHERE pp.partner.id = :partnerId")
    List<OperationalGroup> findByPartnerId(@Param("partnerId") UUID partnerId);
}
