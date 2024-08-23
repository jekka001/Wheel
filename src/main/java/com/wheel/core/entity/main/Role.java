package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

import static com.wheel.core.utils.TableConstants.ROLE_TABLE;

@Data
@Entity
@Table(name = ROLE_TABLE)
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
