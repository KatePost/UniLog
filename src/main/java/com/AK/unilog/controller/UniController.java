package com.AK.unilog.controller;

import com.AK.unilog.entity.User;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView login(){
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("user", new User());
        return mav;
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid User user, BindingResult bindingResult, RedirectAttributes redirAttrs){
        if(bindingResult.hasErrors()){
            return "/register";
        }
        userService.validateRegistration(bindingResult, user);
        if(bindingResult.hasErrors()){
            return "/register";
        }
        System.out.println("no errors");
        userService.saveUser(user, User.Role.STUDENT);
        redirAttrs.addFlashAttribute("message", "Your account has been created!");
        return "redirect:/login";
    }

    @GetMapping("/createAccount")
    public String createAccountPage(){
        return "createAccount";
    }

}
