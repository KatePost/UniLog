package com.AK.unilog.repository;

import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<List<PaymentRecord>> findByUser(User student);
}
