package com.AK.unilog.service;

import com.AK.unilog.entity.User;
import com.AK.unilog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

@Service
public class UserService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder pwEncoder;

    @Autowired
    public UserService(UserRepo userRepo, BCryptPasswordEncoder pwEncoder){
        this.userRepo = userRepo;
        this.pwEncoder = pwEncoder;

    }

    public User findByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public User saveUser(User user, User.Role role){
        user.setPassword(pwEncoder.encode(user.getPassword()));
        user.setRole(role);
        return userRepo.save(user);
    }

    public void validateRegistration(BindingResult bindingResult, User user) {
        LocalDate date = LocalDate.now().minusYears(18);
        if(user.getBirthdate().isAfter(date)){
            bindingResult.rejectValue("birthdate", "age.invalid", "You must be 18 years old to register");
        }
        if(!user.getPassword().equals(user.getPasswordMatch())){
            bindingResult.rejectValue("password", "no.match", "Passwords must match");
        }
        if(userRepo.findByEmail(user.getEmail()) != null){
            bindingResult.rejectValue("email", "already.in.use", "That email already has an associated account");
        }
    }

}
