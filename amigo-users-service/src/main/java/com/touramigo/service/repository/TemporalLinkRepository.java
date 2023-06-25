package com.touramigo.service.repository;

import com.touramigo.service.entity.enumeration.TemporalLinkType;
import com.touramigo.service.entity.user.TemporalLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemporalLinkRepository extends JpaRepository<TemporalLink, UUID> {

    @Query("SELECT t FROM TemporalLink t WHERE t.expiredAt > current_timestamp AND t.user.id = :userId")
    Optional<TemporalLink> findNotExpiredByUser(@Param("userId") UUID userId);

    Optional<TemporalLink> findByUserId(UUID userId);

    @Query("SELECT t FROM TemporalLink t WHERE t.expiredAt > current_timestamp AND t.token = :token")
    Optional<TemporalLink> findNotExpiredByToken(@Param("token") String token);


    Optional<TemporalLink> findFirstByUser_IdAndTypeAndExpiredAtGreaterThan(@Param("userId") UUID userId, @Param("type") TemporalLinkType type, @Param("expiredAt") Date expiredAt);

    Optional<TemporalLink> findById(UUID id);
}
