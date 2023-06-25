package com.touramigo.service.security;

import com.touramigo.service.entity.user.User;
import com.touramigo.service.util.SecurityUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public UUID getCurrentAuditor() {
        User user = SecurityUtil.extractCurrentUser(SecurityUtil.getAuthentication());
        return user == null ? null : user.getId();
    }
}
