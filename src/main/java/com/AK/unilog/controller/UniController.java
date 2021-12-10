package com.AK.unilog.controller;

import com.AK.unilog.entity.User;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UniController {

    private UserService userService;

    @Autowired
    public UniController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/register";
        }
        userService.validateRegistration(bindingResult, user);
        if(bindingResult.hasErrors()){
            return "/register";
        }
        System.out.println("no errors");
        userService.saveUser(user, User.Role.valueOf("STUDENT"));
        return "redirect:/login";
    }

    @GetMapping("/createAccount")
    public String createAccountPage(){
        return "createAccount";
    }

}
