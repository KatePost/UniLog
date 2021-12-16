package com.AK.unilog.service;

import com.AK.unilog.entity.PaymentItem;
import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.PaymentItemRepository;
import com.AK.unilog.repository.PaymentRecordRepository;
import com.AK.unilog.repository.UserRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private PaymentRecordRepository paymentRecordRepository;
    private final UserRepo userRepo;
    private final PaymentItemRepository paymentItemRepository;

    @Autowired
    public CheckoutService(PaymentRecordRepository paymentRecordRepository, UserRepo userRepo, PaymentItemRepository paymentItemRepository){
        this.paymentRecordRepository = paymentRecordRepository;
        this.userRepo = userRepo;
        this.paymentItemRepository = paymentItemRepository;
    }

    private final String STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY");
    public User createCustomer(User user)  {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstName() + " " + user.getLastName());
        params.put("email", user.getEmail());
        try {
            Customer customer = Customer.create(params);
            user.setStripeId(customer.getId());
            user = userRepo.save(user);
            return user;
        }catch (StripeException ex){
            return null;
        }

    }

    public PaymentRecord saveCharge(Charge charge, User student, double total) {

        PaymentRecord paymentRecord = new PaymentRecord();

        paymentRecord.setPaymentDate(LocalDate.now());
        paymentRecord.setTotalPayment(total);
        paymentRecord.setUser(student);
        paymentRecord.setStripePaymentId(charge.getId());

        paymentRecordRepository.save(paymentRecord);

        return paymentRecord;
    }

    public void updatePaymentPrice(List<PaymentItem> paymentItemList) {
        for (PaymentItem paymentItem : paymentItemList){
            //send the price with interest included
            paymentItem.setPaymentPrice(paymentItem.getRegisteredCourse().getFee());

            paymentItem = paymentItemRepository.save(paymentItem);
        }
    }
}

