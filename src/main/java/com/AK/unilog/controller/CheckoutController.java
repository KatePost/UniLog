package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.service.CheckoutService;
import com.AK.unilog.service.CourseService;
import com.AK.unilog.service.PaymentItemService;
import com.AK.unilog.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;


@Controller
public class CheckoutController {

    private final String STRIPE_PUBLIC_KEY = System.getenv("STRIPE_PUBLIC_KEY");

    private final String STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY");

    private final UserService userService;
    private final CheckoutService checkoutService;
    private final PaymentItemService paymentItemService;

    @Autowired
    public CheckoutController(UserService userService, CheckoutService checkoutService, PaymentItemService paymentItemService){
        this.userService = userService;
        this.checkoutService = checkoutService;
        this.paymentItemService = paymentItemService;
    }

    @GetMapping("/payment/clear")
    public String clearCart(Principal principal, RedirectAttributes redirectAttributes){
        User student = userService.findByEmail(principal.getName());
        Optional<List<PaymentItem>> cartList = paymentItemService.getCart(student);
        if(cartList.isPresent()){
            List<PaymentItem> itemList = cartList.get();
            paymentItemService.clearCart(itemList);
        }
        redirectAttributes.addFlashAttribute("message", "Payment cart cleared");
        return "redirect:/student/registeredCourses";
    }

    @PostMapping("/payment/charge")
    public String chargeCard(@RequestParam(name = "stripeToken") String stripeToken, RedirectAttributes redirectAttributes, Principal principal){
        Stripe.apiKey = STRIPE_SECRET_KEY;

        User student = userService.findByEmail(principal.getName());
        double total = paymentItemService.getTotal(student);
        long totalInCents = (long)(total * 100);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", totalInCents);
        params.put("currency", "cad");
        params.put("source", stripeToken);

        try {
            Charge charge = Charge.create(params);

            System.out.println("after charge");

            //save chargeId and create paymentRecord
            PaymentRecord paymentRecord = checkoutService.saveCharge(charge, student, total);

            //update registered courses to reflect payment
            paymentItemService.updateKeyForRegisteredCourses(paymentRecord);

            //remove items from cart
            paymentItemService.clearCart(student);

            redirectAttributes.addFlashAttribute("message", "token success");
            return "redirect:/student/home";
        }catch(StripeException ex){
            ex.printStackTrace();
            ex.getMessage();
            redirectAttributes.addFlashAttribute("deleteMsg", "There was a problem");
            return "redirect:/student/home";
        }
    }

    @GetMapping("/student/makePayment")
    public String makePayment(Model model, Principal principal, RedirectAttributes redirectAttributes){

        User student = userService.findByEmail(principal.getName());
        Optional<List<PaymentItem>> paymentItemListOptional =  paymentItemService.getCart(student);

        if(paymentItemListOptional.isPresent()){
            List<PaymentItem> paymentItemList = paymentItemListOptional.get();

            checkoutService.updatePaymentPrice(paymentItemList);

            if(student.getStripeId() == null){
                checkoutService.createCustomer(student);
                if(student.getStripeId() == null){
                    return "error";
                }
            }
            double total = paymentItemService.getTotal(paymentItemListOptional.get());
            model.addAttribute("paymentItemList", paymentItemList);
            model.addAttribute("total", total);
            return "student/makePayment";
        }
        redirectAttributes.addFlashAttribute("deleteMsg", "Your payment cannot proceed at this time.");
        return "redirect:/student";
    }

}
