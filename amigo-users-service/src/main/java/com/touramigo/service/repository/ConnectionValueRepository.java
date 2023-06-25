package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.ConnectionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionValueRepository extends JpaRepository<ConnectionValue, UUID> {

    Optional<ConnectionValue> findById(UUID id);
}
