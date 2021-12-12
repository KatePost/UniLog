package com.AK.unilog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/*
Note* Must determine a way to specify that all of these urls as /student/{url}
and specify the files as /student/{htmlFileName}
 */

@Controller
@RequestMapping("/student/*")
public class StudentController {

    @GetMapping("home")
    public String home(){
        return "/student/home";
    }

    @GetMapping("availableCourses")
    public String availableCourses(){
        return "/student/availableCourses";
    }

    @GetMapping("cart")
    public String showCart(){
        return "/student/cart";
    }

    @PostMapping("checkout")
    public String checkout(){
        return "/student/checkout";
    }

    @PostMapping("paymentConfirmation")
    public String confirmPayment() {
        return "/student/paymentConfirmation";
    }

    @GetMapping("registeredCourses")
    public String registeredCourses(){
        return "/student/registeredCourses";
    }

    @GetMapping("paymentHistory")
    public String paymentHistory(){
        return "/student/paymentHistory";
    }
}
