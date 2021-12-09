package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

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

}
