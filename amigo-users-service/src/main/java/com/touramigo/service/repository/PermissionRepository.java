package com.touramigo.service.repository;

import com.touramigo.service.entity.global_config.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findById(UUID id);

    @Query("SELECT permission FROM Permission permission WHERE permission.product.id = :productId")
    List<Permission> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT permission FROM Permission permission WHERE permission.group.id = :groupId")
    List<Permission> findByPermissionGroup(@Param("groupId") UUID groupId);
}
