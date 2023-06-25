package com.touramigo.service.security;

import com.touramigo.service.entity.enumeration.ContainerClass;
import com.touramigo.service.entity.enumeration.Permission;
import com.touramigo.service.entity.global_config.AbstractAuditingEntity;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.repository.ContactRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.service.SupplierService;
import com.touramigo.service.util.SecurityUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccessGuard {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ContactRepository contactRepository;

    public void verifyUserCanAccessResource(UUID resourcePartnerId, Permission fullAccessAuthority) {
        verifyUserCanAccessResource(resourcePartnerId, SecurityUtil.getAuthentication(), fullAccessAuthority);
    }

    public void verifyUserCanAccessResource(UUID resourcePartnerId, Authentication authentication, Permission fullAccessAuthority) {
        verifyUserCanAccessResource(Collections.singletonList(resourcePartnerId), authentication, fullAccessAuthority);
    }

    public void verifyUserCanAccessResource(List<UUID> resourcePartnerIds, Permission fullAccessAuthority) {
        verifyUserCanAccessResource(resourcePartnerIds, SecurityUtil.getAuthentication(), fullAccessAuthority);
    }

    public void verifyUserCanAccessResource(List<UUID> resourcePartnerIds, Authentication authentication, Permission fullAccessAuthority) {
        if (Objects.nonNull(fullAccessAuthority) && SecurityUtil.isCurrentUserInRole(authentication, fullAccessAuthority.getCodeKey())) {
            return;
        }

        AuthUserDetail authUserDetail = SecurityUtil.extractAuthUserDetail(authentication);
        if (authUserDetail != null) {
            UUID currentPartnerId = authUserDetail.getPartnerId();
            if (resourcePartnerIds.contains(currentPartnerId)) {
                return;
            }
        }

        throw new InvalidDataException("You are not allowed to modify data of another partner");
    }

    public void validateUserHasReadAccess(AbstractAuditingEntity abstractAuditingEntity) {
        UUID authorUuid = abstractAuditingEntity.getCreatedBy();
        User author = userRepository.findById(authorUuid)
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));
        UUID authorPartnerId = author.getPartner().getId();
        UUID currentPartnerId = SecurityUtil.getPartnerId();

        if (!Objects.equals(authorPartnerId, currentPartnerId)) {
            throw new AccessDeniedException(UserServiceErrorMessages.ACCESS_DENIED);
        }
    }

    public void validateUserHasModifyAccess(AbstractAuditingEntity abstractAuditingEntity) {
        UUID authorId = abstractAuditingEntity.getCreatedBy();
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!Objects.equals(authorId, currentUserId)) {
            throw new AccessDeniedException(UserServiceErrorMessages.ACCESS_DENIED);
        }
    }
}
