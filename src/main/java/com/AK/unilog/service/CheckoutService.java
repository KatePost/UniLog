package com.AK.unilog.service;

import com.AK.unilog.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CheckoutService {

    private final String STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY");
    public User createCustomer(User user)  {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstName() + " " + user.getLastName());
        params.put("email", user.getEmail());
        try {
            Customer customer = Customer.create(params);
            user.setStripeId(customer.getId());
            return user;
        }catch (StripeException ex){
            return null;
        }

    }
}

//    public Customer createCustomer() throws StripeException {
//        CustomerCreateParams params =
//                CustomerCreateParams
//                        .builder()
//                        .setDescription("My First Test Customer")
//                        .build();
//
//        Customer customer = Customer.create(params);
//        return customer;
//    }
//
//    public PaymentIntent createPaymentIntent(Customer customer, Long total) throws StripeException {
//        PaymentIntentCreateParams params =
//                PaymentIntentCreateParams
//                        .builder()
//                        .setCustomer(customer.getId())
//                        .setAmount(total)
//                        .addPaymentMethodType("card")
//                        .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.ON_SESSION)
//                        .build();
//
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
//        return paymentIntent;
//    }