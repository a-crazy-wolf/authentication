package com.learning.authentication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
public class AuthUserDetail extends User implements UserDetails {

    private static final long serialVersionUID = -1;

    public AuthUserDetail(User user){
        super(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        getRoles().forEach(role -> {
            grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
            role.getPermissions().forEach(permission -> grantedAuthorityList.add(new SimpleGrantedAuthority(permission.getName())));
        });
        return grantedAuthorityList;
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Override
    public String getEmailId() {
        return super.getEmailId();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public Integer getUserType() {
        return super.getUserType();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public String getMfaSecret() {
        return super.getMfaSecret();
    }

    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public int getFailedAttempt() {
        return super.getFailedAttempt();
    }
}
