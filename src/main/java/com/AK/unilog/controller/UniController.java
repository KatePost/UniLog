package com.AK.unilog.controller;

import com.AK.unilog.entity.User;
import com.AK.unilog.model.RecoverPasswordForm;
import com.AK.unilog.service.LoginService;
import com.AK.unilog.service.PasswordRecoveryService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UniController {

    private final UserService userService;
    private final LoginService loginService;
    private final PasswordRecoveryService passwordRecoveryService;

    @Autowired
    public UniController(UserService userService, LoginService loginService, PasswordRecoveryService passwordRecoveryService){
        this.userService = userService;
        this.loginService = loginService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @GetMapping({"/", "home"})
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

    @GetMapping("/recoverPassword")
    public String showRecoverPassword(){
        return "recoverPassword";
    }

    @PostMapping("/recoverPassword")
    public String recoverPassword(@RequestParam("email") String email, Model model){
       User user = userService.findByEmail(email);
       if(user != null){
           passwordRecoveryService.recoverPassword(user);
       }
       model.addAttribute("message", "If an account matching that email is found, it will be emailed the next steps for password recovery");
        return "recoverPassword";
    }

    @GetMapping("/passwordReset/{resetToken}")
    public String passwordReset(@PathVariable("resetToken") String resetToken, Model model, RedirectAttributes redirectAttributes){
        User user = userService.findByUuid(resetToken);
        if(user != null){
            if(user.getRecoveryExpiration().isBefore(LocalDateTime.now())){
                redirectAttributes.addFlashAttribute("errorMsg", "The link you're trying to access is either expired or incorrect.");
                return "redirect:/login";
            }
            RecoverPasswordForm recoverPasswordForm = new RecoverPasswordForm();
            recoverPasswordForm.setUuid(resetToken);
            recoverPasswordForm.setEmail(user.getEmail());
            model.addAttribute("form", recoverPasswordForm );
            return "recoverPasswordForm";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "The link you're trying to access is either expired or incorrect.");
        return "redirect:/login";
    }

    @PostMapping("/passwordReset")
    public String passwordReset(RecoverPasswordForm recoverPasswordForm, RedirectAttributes redirectAttributes){
        User user = userService.findByUuid(recoverPasswordForm.getUuid());

        if(user.getEmail().equals(recoverPasswordForm.getEmail())){
            System.out.println("user email matches token");
            if (passwordRecoveryService.validateNewPassword(user, recoverPasswordForm)){
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("messageSuccess", "Password updated successfully");
                return "redirect:/login";
            }
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Could not authenticate credentials.");
        return "redirect:/login";
    }
}
