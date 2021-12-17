package com.AK.unilog.service;

import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentRecordService {

    private final PaymentRecordRepository paymentRecordRepository;

    @Autowired
    public PaymentRecordService(PaymentRecordRepository paymentRecordRepository){
        this.paymentRecordRepository = paymentRecordRepository;
    }

    public List<PaymentRecord> findRecordsByStudent(User student){
        Optional<List<PaymentRecord>> paymentRecordsOptional = paymentRecordRepository.findByUser(student);
        return paymentRecordsOptional.orElse(null);
    }

    public PaymentRecord findById(Long paymentId) {
        Optional<PaymentRecord> paymentRecordOptional = paymentRecordRepository.findById(paymentId);
        return paymentRecordOptional.orElse(null);
    }

}
