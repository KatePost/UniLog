package com.AK.unilog.controller;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.User;
import com.AK.unilog.service.RegistrationService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
Note* Must determine a way to specify that all of these urls as /student/{url}
and specify the files as /student/{htmlFileName}
 */

@Controller
@RequestMapping("/student/*")
public class StudentController {

    private UserService userService;
    private RegistrationService registrationService;

    @Autowired
    public StudentController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @GetMapping("home")
    public String home(){
        return "student/home";
    }

    @GetMapping("availableCourses")
    public String availableCourses(){
        return "student/availableCourses";
    }

    @GetMapping("cart")
    public String showCart(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        Set<CartItem>cart = new HashSet<>(student.getCart());
        model.addAttribute("cart", cart);
        //get the subtotal of all the cart items
        double total = 0;
        for(CartItem item : cart){
            total += item.getSection().getCourse().getPrice();
        }
        model.addAttribute("total", total);
        //figure out what the due date is
        //student and section can be passed from cart item.
        //these cart items have to disappear when the post is made
        return "student/cart";
    }

    @PostMapping("checkout")
    public String checkout(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());

        //FIXME: this is not ideal - showing errors, not successes.
        ArrayList<String> errorMsgs = registrationService.registerCart(student.getCart());
        model.addAttribute("errorMsgs", errorMsgs);
        return "student/checkout";
    }

    @PostMapping("paymentConfirmation")
    public String confirmPayment() {
        return "student/paymentConfirmation";
    }

    @GetMapping("registeredCourses")
    public String registeredCourses(){
        return "student/registeredCourses";
    }

    @GetMapping("paymentHistory")
    public String paymentHistory(){
        return "student/paymentHistory";
    }
}
