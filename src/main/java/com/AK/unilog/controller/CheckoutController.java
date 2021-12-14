//package com.AK.unilog.controller;
//
//import com.stripe.exception.StripeException;
//import com.stripe.model.Customer;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.CustomerCreateParams;
//import com.stripe.param.PaymentIntentCreateParams;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class CheckoutController {
//
//    @Value("${STRIPE_PUBLIC_KEY}")
//    private String stripePublicKey;
//
//    @Value("${STRIPE_SECRET_KEY}")
//    private String stripeSecretKey;
//
//    public CheckoutController() throws StripeException {
//    }
//
////    @RequestMapping("/checkout")
////    public String checkout(Model model) {
////        model.addAttribute("amount", 50 * 100); // in cents
////        model.addAttribute("stripePublicKey", stripePublicKey);
////        return "checkout";
////    }
//
//    CustomerCreateParams params =
//            CustomerCreateParams
//                    .builder()
//                    .setDescription("My First Test Customer")
//                    .build();
//
//    Customer customer = Customer.create(params);
//
//    PaymentIntentCreateParams params =
//            PaymentIntentCreateParams
//                    .builder()
//                    .setCustomer("{{CUSTOMER_ID}}")
//                    .setAmount(2000L)
//                    .addPaymentMethodType("card")
//                    .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.ON_SESSION)
//                    .build();
//
//    PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//}