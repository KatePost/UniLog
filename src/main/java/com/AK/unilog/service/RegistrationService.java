package com.AK.unilog.service;

import com.AK.unilog.repository.RegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private RegistrationRepo registrationRepo;

    @Autowired
    public RegistrationService(RegistrationRepo registrationRepo) {
        this.registrationRepo = registrationRepo;
    }

    public RegistrationRepo getRegistrationRepo() {
        return registrationRepo;
    }

}
