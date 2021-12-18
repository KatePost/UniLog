package com.AK.unilog.repository;

import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<RegisteredCourse, Long> {

    Optional<RegisteredCourse> findBySectionAndUser(Section section, User user);
    Optional<List<RegisteredCourse>> findByPaymentRecord(PaymentRecord paymentRecord);
    Optional<List<RegisteredCourse>> findByUser(User user);

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.paymentRecord IS NULL and r.user = ?1")
    Optional<List<RegisteredCourse>> findUnpaidByUser(User user);

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.paymentRecord IS NOT NULL and r.user = ?1")
    Optional<List<RegisteredCourse>> findPaidByUser(User user);

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.dueDate < CURRENT_DATE and r.user = ?1")
    Optional<List<RegisteredCourse>> findPastByUser(User user);

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.dueDate > CURRENT_DATE and r.user = ?1")
    Optional<List<RegisteredCourse>> findUpcomingByUser(User user);

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.paymentRecord IS NULL")
    Optional<List<RegisteredCourse>> findUnpaid();

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.paymentRecord IS NOT NULL")
    Optional<List<RegisteredCourse>> findPaid();

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.dueDate < CURRENT_DATE")
    Optional<List<RegisteredCourse>> findPast();

    @Query(value = "SELECT r FROM RegisteredCourse r WHERE r.dueDate > CURRENT_DATE")
    Optional<List<RegisteredCourse>> findUpcoming();


}
