package com.touramigo.service.repository;

import com.touramigo.service.entity.supplier.SupplierConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SupplierConnectionRepository extends JpaRepository<SupplierConnection, UUID> {

    Optional<SupplierConnection> findById(UUID id);
}
