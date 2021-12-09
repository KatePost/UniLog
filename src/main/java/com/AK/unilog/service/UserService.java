package com.AK.unilog.service;

import com.AK.unilog.entity.User;
import com.AK.unilog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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


}
