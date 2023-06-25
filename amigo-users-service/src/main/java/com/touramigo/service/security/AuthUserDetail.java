package com.touramigo.service.security;

import com.touramigo.service.entity.user.User;
import com.touramigo.service.util.SecurityUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Transactional
public class AuthUserDetail extends User implements UserDetails {

    User currentUser;
    UUID partnerId;


    public AuthUserDetail(User user) {
        super(user);
        this.currentUser = user;
        this.partnerId = user.getPartner().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

       SecurityUtil.getUsersPermissionsCodeKeys(this.currentUser).forEach(permission -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission));
        });

        return grantedAuthorities;
    }

    public UUID getPartnerId() {
        return this.partnerId;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {

        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNotExpired();
    }

    @Override
    public boolean isEnabled() {
        if (!this.currentUser.getPartner().isEnabled()) {
            return false;
        }
        return super.isEnabled();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
