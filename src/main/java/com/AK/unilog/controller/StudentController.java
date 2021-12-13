package com.AK.unilog.controller;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.User;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
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

    @Autowired
    public StudentController(UserService userService) {
        this.userService = userService;
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
        System.err.println(student.getCart());
        List<CartItem>cart = new ArrayList<>(student.getCart());
        System.err.println(cart);
        model.addAttribute("cart", cart);
        return "student/cart";
    }

    @PostMapping("checkout")
    public String checkout(){
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
