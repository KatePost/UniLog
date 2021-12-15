package com.AK.unilog.controller;

import com.AK.unilog.entity.User;
import com.AK.unilog.service.LoginService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UniController {

    private UserService userService;
    private LoginService loginService;

    @Autowired
    public UniController(UserService userService, LoginService loginService){
        this.userService = userService;
        this.loginService = loginService;
    }

    @GetMapping("/")
    public String home(){
        Set<String>roles = loginService.getRoles();
        if(roles.contains("STUDENT")){
            return "redirect:/student/";
        }
        if(roles.contains("ADMIN")){
            return "redirect:/admin/";
        }
        return "index";
    }

    @GetMapping("/login") //login is taken care of by spring security. no model necessary
    public String login(){
        return "login";
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

    @RequestMapping("/default")
    public String defaultAfterLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Login Successful");
        if (loginService.getRoles().contains("STUDENT")) {
            return "redirect:/student/home";
        }
        return "redirect:/admin/home";
    }

//    @GetMapping("/home")
//    public String getHome(){
//        return "redirect:/";
//    }
}
