package com.AK.unilog.service;

import com.AK.unilog.entity.*;
import com.AK.unilog.repository.PaymentItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentItemService {

    private final PaymentItemRepository paymentItemRepository;

    @Autowired
    public PaymentItemService(PaymentItemRepository paymentItemRepository){
        this.paymentItemRepository = paymentItemRepository;
    }

    public void verifyPaymentItems(List<RegisteredCourse> registeredList, User student) {
        clearCart(student);
        for(RegisteredCourse course : registeredList){
            Optional<PaymentItem> registeredCourse = paymentItemRepository.findByRegisteredCourseAndStudent(course, student);
            if(registeredCourse.isPresent()){
                continue;
            }
            PaymentItem paymentItem = new PaymentItem();
            paymentItem.setStudent(student);
            paymentItem.setRegisteredCourse(course);
            paymentItemRepository.save(paymentItem);
        }
    }

    public Optional<List<PaymentItem>> getCart(User student) {
        Optional<List<PaymentItem>> paymentItemList = paymentItemRepository.findByStudent(student);
        return paymentItemList;
    }

    public double getTotal(List<PaymentItem> paymentItemList) {
        double total = 0;
        for(PaymentItem item : paymentItemList){
            total += item.getPaymentPrice();
        }
        return total;
    }

    public double getTotal(User student) {
        Optional<List<PaymentItem>> paymentItemList = paymentItemRepository.findByStudent(student);
        double total = 0;
        if(paymentItemList.isPresent()) {
            System.out.println("is present");
            List<PaymentItem> paymentList = paymentItemList.get();
            for (PaymentItem item : paymentList) {
                System.out.println(item);
                total += item.getPaymentPrice();
            }
        }
        return total;
    }

    public void clearCart(List<PaymentItem> itemList) {
        paymentItemRepository.deleteAll(itemList);
    }

    public void updateKeyForRegisteredCourses(PaymentRecord paymentRecord){
        Optional<List<PaymentItem>> paymentItemList = paymentItemRepository.findByStudent(paymentRecord.getUser());
        if(paymentItemList.isPresent()){
            List<PaymentItem> paymentList = paymentItemList.get();
            for(PaymentItem paymentItem : paymentList){
                paymentItem.getRegisteredCourse().setPaymentRecord(paymentRecord);
            }
        }
    }

    public void clearCart(User student) {
        Optional<List<PaymentItem>> paymentItemList = paymentItemRepository.findByStudent(student);
        if(paymentItemList.isPresent()){
            List<PaymentItem> paymentList = paymentItemList.get();
            paymentItemRepository.deleteAll(paymentList);
        }
    }
}
