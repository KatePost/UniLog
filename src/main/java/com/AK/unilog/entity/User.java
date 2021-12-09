package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    @Size(min = 3, max = 200)
    @NotEmpty
    private String firstName;

    @Column(length = 200)
    @Size(min = 3, max = 200)
    @NotEmpty
    private String lastName;

    @Column(length = 400)
    @Size(min = 20, max = 400)
    @NotEmpty
    private String address;

    @NotEmpty
    private LocalDate birthdate;

    @NotNull
    @Column(length = 64, nullable = false)
    private String password;

    @NotNull
    @Column(length = 150, nullable = false)
    @Size(min = 6, max = 150)
    private String email;

    private Role role;

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority>authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Role {
        STUDENT, ADMIN
    }
}
