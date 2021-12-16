package com.AK.unilog.repository;

import com.AK.unilog.entity.PaymentItem;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

    Optional<PaymentItem> findByRegisteredCourseAndStudent(RegisteredCourse course, User student);

    Optional<List<PaymentItem>> findByStudent(User student);
}
