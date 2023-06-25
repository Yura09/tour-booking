package com.touramigo.service.repository;

import com.touramigo.service.entity.partner.PartnerProduct;
import com.touramigo.service.entity.user.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProductRepository extends JpaRepository<UserProduct, UUID> {
    Optional<UserProduct> findById(UUID id);

    @Query("SELECT up FROM UserProduct up WHERE up.user.id = :userId")
    List<UserProduct> findByUserId(@Param("userId") UUID userId);
}
