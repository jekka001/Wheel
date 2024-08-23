package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.wheel.core.utils.TableConstants.*;

@Data
@Entity
@Table(name = USER_TABLE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String login;
    private String password;
    private boolean deactivate;
    @ManyToMany
    @JoinTable(
            name = USER_ROLE_TABLE,
            joinColumns = @JoinColumn(name = USER_ID_FIELD),
            inverseJoinColumns = @JoinColumn(name = ROLE_ID_FIELD)
    )
    private List<Role> roles;

    public User() {
    }

    public User(String login) {
        this.login = login;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !deactivate;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deactivate;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !deactivate;
    }

    @Override
    public boolean isEnabled() {
        return !deactivate;
    }
}
