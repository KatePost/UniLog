package com.AK.unilog.model;

import com.AK.unilog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecoverPasswordForm {

    private String email;

    private String uuid;

    private String password;

    private String passwordMatch;

}
