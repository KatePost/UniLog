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

    @Column(length = 7, nullable = false)
    @Size(min = 7, max = 7)
    private String courseNumber;

    @Column(length = 500, nullable = false)
    @NotEmpty
    @NotNull
    private String description;

    @Column(columnDefinition ="decimal(5,2)", nullable = false)
    @Min(50)
    @NotNull
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prereq_id", nullable = false)
    private Course course;

}
