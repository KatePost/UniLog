package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
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
    @Size(min = 2, max = 200, message = "Please enter a valid name")
    private String firstName;

    @Column(length = 200)
    @Size(min = 2, max = 200, message = "Please enter a valid name")
    private String lastName;

    @Column(length = 400)
    @Size(min = 20, max = 400, message = "Please enter a full address")
    private String address;

    @NotNull
    @Past
    private LocalDate birthdate;

    @Column(length = 64, nullable = false)
    private String password;

    @Transient
    @Pattern(regexp = "^[A-Za-z0-9]{6,15}$", message = "Password must be 6 - 15 characters with only alphanumeric values")
    private String passwordMatch;

    @Column(length = 150, nullable = false, unique = true)
    @Size(min = 6, max = 150, message = "Please enter a valid email")
    @Email(message = "Please enter a valid email")
    private String email;

    @Enumerated(EnumType.STRING)
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
