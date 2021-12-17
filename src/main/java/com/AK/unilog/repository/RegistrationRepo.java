package com.AK.unilog.repository;

import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<RegisteredCourse, Long> {

    Optional<RegisteredCourse> findBySectionAndUser(Section section, User user);

    Optional<List<RegisteredCourse>> findByPaymentRecord(PaymentRecord paymentRecord);
}
