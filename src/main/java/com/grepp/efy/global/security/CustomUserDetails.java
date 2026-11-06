package com.grepp.efy.global.security;

import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NullMarked
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String email;
    private final @Nullable String password;
    private final MemberRole role;
    private final long tokenVersion;

    public CustomUserDetails(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole();
        this.tokenVersion = member.getTokenVersion();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name())); // ROLE_USER, ROLE_EXPERT, ROLE_ADMIN
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
