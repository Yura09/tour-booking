package com.touramigo.service.util;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.UnauthorizedException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.security.AuthUserDetail;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@UtilityClass
public class SecurityUtil {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);

    public List<String> getUsersPermissionsCodeKeys(User currentUser) {
        if (Objects.nonNull(currentUser.getPartner()) && Objects.nonNull(currentUser.getPartner().getPartnerProducts()) && Objects.nonNull(currentUser.getUserProducts())) {
            Set<String> partnerPermissions = currentUser.getPartner().getPartnerProducts().stream()
                .flatMap(partnerProduct -> partnerProduct.getPermissions().stream().map(Permission::getCodeKey))
                .collect(Collectors.toSet());

            List<String> userPermissions = currentUser.getUserProducts().stream()
                .flatMap(partnerProduct -> partnerProduct.getPermissions().stream()
                    .map(Permission::getCodeKey))
                .collect(Collectors.toList());

            List<String> userGroupPermissions = currentUser.getOperationalGroups().stream()
                .flatMap(oGroup -> oGroup.getPermissions().stream()
                    .map(Permission::getCodeKey))
                .collect(Collectors.toList());

            List<String> permissions = Stream.concat(userPermissions.stream(), userGroupPermissions.stream()).distinct().collect(Collectors.toList());

            List<String> finalPermissions = permissions.stream()
                .filter(partnerPermissions::contains) //Only allow permissions assigned to a partner
                .collect(Collectors.toList());

            return finalPermissions;

        } else {
            return Collections.emptyList();
        }
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static UUID getPartnerId() {
        return getCurrentUser().getPartner().getId();
    }

    public static User getCurrentUser() {
        User user = extractCurrentUser(getAuthentication());
        if (user == null) {
            throw new UnauthorizedException(UserServiceErrorMessages.NOT_AUTHORIZED);
        }

        return user;
    }

    public static User extractCurrentUser(Authentication authentication) {
        AuthUserDetail authUserDetail = extractAuthUserDetail(authentication);
        if (authUserDetail != null) {
            return authUserDetail.getCurrentUser();
        }

        return null;
    }

    public static AuthUserDetail extractAuthUserDetail(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetail) {
            return (AuthUserDetail) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean isCurrentUserInRole(String authority) {
        return isCurrentUserInRole(getAuthentication(), authority);
    }

    public static boolean isCurrentUserInRole(Authentication authentication, String authority) {
        return authentication != null && getAuthorities(authentication).anyMatch(authority::equals);
    }

    public static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority);
    }

    public static String extractToken() {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) getAuthentication().getDetails();
        return details.getTokenType() + " " + details.getTokenValue();

    }
}
