package com.AK.unilog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/home")
    public String home(){
        return "/admin/home";
    }

    @GetMapping("/courses")
    public String showCourses(){
        return "/admin/courses";
    }

    @GetMapping("/newCourse")
    public String newCoursePage(){
        return "/admin/newCourse";
    }

    @GetMapping("/registrations")
    public String showRegistrations(){
        return "/admin/registrations";
    }

    @GetMapping("/register")
    public String register(){
        return "/admin/register";
    }

    @GetMapping("/outstanding")
    public String showOutstandingFees(){
        return "/admin/outstanding";
    }
}
