package com.AK.unilog.service;

import com.AK.unilog.entity.User;
import com.AK.unilog.model.RecoverPasswordForm;
import com.AK.unilog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    private final SendGridEmailService sendGridEmailService;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordRecoveryService(SendGridEmailService sendGridEmailService, UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.sendGridEmailService = sendGridEmailService;
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void recoverPassword(User user) {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setRecoveryUuid(uuid);
        user.setRecoveryExpiration(LocalDateTime.now().plusMinutes(5));
        userRepo.save(user);

        String link = "https://uni-log.herokuapp.com/passwordReset/" + user.getRecoveryUuid();
        sendGridEmailService.sendRecoveryMail(user, link);
    }

    public boolean validateNewPassword(User user, RecoverPasswordForm recoverPasswordForm) {
        String newPassword = recoverPasswordForm.getPassword();
        if(newPassword.equals(recoverPasswordForm.getPasswordMatch()) && newPassword.matches("^[A-Za-z0-9]{6,15}$")){
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            return true;
        }
        return false;
    }
}
