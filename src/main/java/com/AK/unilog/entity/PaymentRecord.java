package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_records")
public class PaymentRecord {
    public enum Semesters{
        SUMMER,
        FALL,
        WINTER,
        SPRING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition ="decimal(8,2)", nullable = false)
    @NotEmpty
    @NotNull
    private double totalPayment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User user;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private Semesters semester;

    @Column(nullable = false)
    @Min(2021)
    @Max(2100)
    private int year;
}
