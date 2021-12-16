package com.AK.unilog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_items")
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "registered_course_id")
    private RegisteredCourse registeredCourse;

    //this needs to take into account interest. This also needs to be recalculated everytime /makePayment is reloaded
    //to get updated info about interest
    @Column
    @Nullable
    private double paymentPrice;
}
