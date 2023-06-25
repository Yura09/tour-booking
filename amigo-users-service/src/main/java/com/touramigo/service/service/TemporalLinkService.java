package com.touramigo.service.service;

import com.touramigo.service.entity.enumeration.TemporalLinkType;
import com.touramigo.service.entity.user.TemporalLink;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface TemporalLinkService {

    Optional<TemporalLink> findByUser(UUID userId);

    @Transactional
    void deleteById(UUID id);

    @Transactional
    Optional<TemporalLink> findNotExpiredByToken(String token);

    @Transactional
    TemporalLink createTemporalLink(UUID userId, TemporalLinkType temporalLinkType);

    String generateToken();

    Optional<TemporalLink> findNotExpiredByUserAndType(UUID userId, TemporalLinkType type);

    @Transactional
    TemporalLink updateTemporalLink(UUID temporalLinkId);
}
