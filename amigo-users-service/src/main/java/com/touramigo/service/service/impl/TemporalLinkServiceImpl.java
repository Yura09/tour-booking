package com.touramigo.service.service.impl;

import com.touramigo.service.entity.enumeration.TemporalLinkType;
import com.touramigo.service.entity.user.TemporalLink;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.repository.TemporalLinkRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.service.TemporalLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TemporalLinkServiceImpl implements TemporalLinkService {

    @Autowired
    private TemporalLinkRepository temporalLinkRepository;

    @Autowired
    private UserRepository userRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_SIZE = 128;
    private static final int HOURS_TO_EXPIRE = 24;

    @Override
    @Transactional
    public Optional<TemporalLink> findByUser(UUID userId) {
        return temporalLinkRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        temporalLinkRepository.delete(id);
    }


    @Override
    @Transactional
    public Optional<TemporalLink> findNotExpiredByToken(String token) {
        return temporalLinkRepository.findNotExpiredByToken(token);
    }

    @Override
    @Transactional
    public TemporalLink createTemporalLink(UUID userId, TemporalLinkType temporalLinkType) {
        TemporalLink temporalLink = new TemporalLink();
        temporalLink.setUser(userRepository.findById(userId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND)));
        temporalLink.setType(temporalLinkType);
        temporalLink.setToken(this.generateToken());
        temporalLink.setExpiredAt(Date.from(LocalDateTime.now().plusHours(HOURS_TO_EXPIRE).atZone(ZoneId.systemDefault()).toInstant()));
        return temporalLinkRepository.save(temporalLink);
    }

    @Override
    public String generateToken() {
        byte[] token = new byte[TOKEN_SIZE];
        SECURE_RANDOM.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    @Override
    public Optional<TemporalLink> findNotExpiredByUserAndType(UUID userId, TemporalLinkType type) {
        return temporalLinkRepository.findFirstByUser_IdAndTypeAndExpiredAtGreaterThan(userId, type, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public TemporalLink updateTemporalLink(UUID temporalLinkId) {
        TemporalLink temporalLink = getEntityById(temporalLinkId);
        temporalLink.setToken(this.generateToken());
        temporalLink.setExpiredAt(Date.from(LocalDateTime.now().plusHours(HOURS_TO_EXPIRE).atZone(ZoneId.systemDefault()).toInstant()));
        return temporalLinkRepository.save(temporalLink);
    }

    private TemporalLink getEntityById(UUID id) {
        return temporalLinkRepository.findById(id).orElseThrow(
                () -> new UnexistedDataException(UserServiceErrorMessages.TEMPORAL_LINK_NOT_FOUND));
    }

}
