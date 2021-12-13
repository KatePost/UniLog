package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 7, nullable = false, unique = true)
    @Size(min = 7, max = 7)
    private String courseNumber;

    @Column(length = 40, nullable = false)
    @NotEmpty
    @NotNull
    @Size(max = 40)
    private String title;

    @Column(length = 500, nullable = false)
    @NotEmpty
    @NotNull
    @Size(max = 500)
    private String description;

    @Column(columnDefinition ="decimal(5,2)", nullable = false)
    @Min(50)
    @NotNull
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prereq_id", columnDefinition="integer")
    private Course prereq;

    @Column(nullable = false)
    private boolean disabled = false;

}
