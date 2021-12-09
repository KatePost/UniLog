package com.AK.unilog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/courses")
    public String showCourses(){
        return "courses";
    }

    @GetMapping("/newCourse")
    public String newCoursePage(){
        return "newCourse";
    }

    @GetMapping("/registrations")
    public String showRegistrations(){
        return "registrations";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/outstanding")
    public String showOutstandingFees(){
        return "outstanding";
    }
}
