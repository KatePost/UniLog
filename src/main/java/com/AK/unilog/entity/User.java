package com.AK.unilog.entity;


import com.AK.unilog.utils.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    @Column(length = 150, unique = true)
    private String stripeId;

    private LocalDateTime recoveryExpiration = null;

    private String recoveryUuid = null;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default false")
    private boolean disabled;


    public User(Long id, String firstName, String lastName, String address, LocalDate birthdate, String password, String passwordMatch, String email, String stripeId, Role role, LocalDateTime recoveryExpiration, String recoveryUuid, boolean disabled) {//, Set<CartItem> cart, Set<RegisteredCourse> registeredCourses
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.password = password;
        this.passwordMatch = passwordMatch;
        this.email = email;
        this.stripeId = stripeId;
        this.role = role;
        this.recoveryExpiration = recoveryExpiration;
        this.recoveryUuid = recoveryUuid;
        this.disabled = disabled;
    }

    public User() {

    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getStripeId() {
        return stripeId;
    }

    public void setStripeId(String stripeId) {
        this.stripeId = stripeId;
    }

    public LocalDateTime getRecoveryExpiration() {
        return recoveryExpiration;
    }

    public void setRecoveryExpiration(LocalDateTime recoveryExpiration) {
        this.recoveryExpiration = recoveryExpiration;
    }

    public String getRecoveryUuid() {
        return recoveryUuid;
    }

    public void setRecoveryUuid(String recoveryUuid) {
        this.recoveryUuid = recoveryUuid;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordMatch() {
        return passwordMatch;
    }

    public void setPasswordMatch(String passwordMatch) {
        this.passwordMatch = passwordMatch;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

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

    /**
     *
     * check the student's registered courses against the section they're trying to get into
     * loop through the student's registered courses. for each registered course, check if it matches the prerequisite.
     * if it does, check that this prerequisite is scheduled on the same date or before the section being passed.
     * since a student may take a course more than once, we must continue checking entries if the prerequisite is found
     * but starts after the passed section
     *
     * @param section
     * @return
     */
    public boolean hasPrerequisite(Section section){

        return Users.hasPrerequisite(this, section);

    }

    public enum Role {
        STUDENT, ADMIN
    }
}
