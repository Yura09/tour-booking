package com.touramigo.security;

import com.touramigo.service.entity.enumeration.Permission;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.security.AccessGuard;
import com.touramigo.service.security.AuthUserDetail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

public class AccessGuardTest {

    @Test
    public void verifyCanAccessResourceWhenAdministratorAuthority() {
        Authentication authentication = Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Permission.ADMINISTRATE_PARTNERS.getCodeKey());
        authorities.add(grantedAuthority);

        when((List<GrantedAuthority>)authentication.getAuthorities()).thenReturn(authorities);

        AccessGuard accessGuard = new AccessGuard();
        accessGuard.verifyUserCanAccessResource((UUID)null, authentication, Permission.ADMINISTRATE_PARTNERS);
    }

    @Test
    public void verifyCanAccessResourceOfUserPartner() {
        UUID resourcePartnerId = UUID.randomUUID();

        Authentication authentication = Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Permission.ADMINISTRATE_PARTNERS.getCodeKey());
        authorities.add(grantedAuthority);

        AuthUserDetail user = Mockito.mock(AuthUserDetail.class);
        when(user.getPartnerId()).thenReturn(resourcePartnerId);

        when((List<GrantedAuthority>)authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getPrincipal()).thenReturn(user);

        AccessGuard accessGuard = new AccessGuard();
        accessGuard.verifyUserCanAccessResource(resourcePartnerId, authentication, null);
    }

    @Test
    public void verifyCanNOTAccessResourceOfUserPartner() {
        Authentication authentication = Mockito.mock(Authentication.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Permission.ADMINISTRATE_PARTNERS.getCodeKey());
        authorities.add(grantedAuthority);

        AuthUserDetail user = Mockito.mock(AuthUserDetail.class);
        when(user.getPartnerId()).thenReturn(UUID.randomUUID());


        when((List<GrantedAuthority>)authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getPrincipal()).thenReturn(user);

        AccessGuard accessGuard = new AccessGuard();
        Throwable e = null;
        try {
            accessGuard.verifyUserCanAccessResource(UUID.randomUUID(), authentication, null);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof InvalidDataException);
    }
}
