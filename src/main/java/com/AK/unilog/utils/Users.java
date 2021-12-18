package com.AK.unilog.utils;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.RegistrationRepo;
import com.AK.unilog.service.CartItemService;
import com.AK.unilog.service.RegistrationService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class Users {

    private static RegistrationService registrationService;
    private static UserService userService;

    @Autowired
    public Users(RegistrationService registrationService, UserService userService){
        Users.registrationService = registrationService;
        Users.userService = userService;
    }


    public static boolean hasPrerequisite(User user, Section section){

        Course prerequisite = section.getCourse().getPrereq();
        if(prerequisite == null){
            return true;
        }

        for(RegisteredCourse registeredCourse : registrationService.findByUser(user)){
            if(registeredCourse.getSection().getCourse().getId() == prerequisite.getId()){
                if(!registeredCourse.getSection().getStartDate().isAfter(section.getStartDate())){
                    return true;
                }
            }
        }
        //check if section start date is at least after registered course start date
        return false;

    }

    public static double getUnpaidSum(User user){

        double price = 0.00;
        for(RegisteredCourse course : registrationService.findByUser(user)){
            price += course.getFee();
        }
        return price;
    }

    public static Map<String, Object> getStringObjectMap(String field, String value, String userEmail, boolean selfEdit) {
        User user = userService.findByEmail(userEmail);
        Map<String, Object> data = new HashMap<>();
        String fieldMsg = "";

        switch (field) {
            case "firstName":
                try {
                    user.setFirstName(value);
                    userService.saveUser(user);
                } catch (TransactionSystemException e) {
                    data.put("success", false);
                    data.put("message", "First name must be between 2 and 200 characters");
                    return data;
                }
                fieldMsg = "First name";
                break;
            case "lastName":
                try {
                    user.setLastName(value);
                    userService.saveUser(user);
                } catch(TransactionSystemException e){
                    data.put("success", false);
                    data.put("message", "Last name must be between 2 and 200 characters");
                    return data;
                }
                fieldMsg = "Last name";
                break;
            case "address":
                try {
                    user.setAddress(value);
                    userService.saveUser(user);
                } catch(TransactionSystemException e){
                    data.put("success", false);
                    data.put("message", "Address must be a valid address format");
                    return data;
                }
                fieldMsg = "Address";
                break;
            case "email":
                if(selfEdit){
                    data.put("success", false);
                    data.put("message", "Cannot update your own email address. Please contact another administrator");
                    return data;
                }
                if(userService.findByEmail(value) != null){
                    data.put("success", false);
                    data.put("message", "Email address is already in use");
                    return data;
                }
                try {
                    user.setEmail(value);
                    userService.saveUser(user);
                } catch(TransactionSystemException e){
                    data.put("success", false);
                    data.put("message", "Email must be a valid email address format of no more than 150 characters");
                    return data;
                }
                fieldMsg = "Email address";
                break;
            case "birthdate":
                if(selfEdit){
                    data.put("success", false);
                    data.put("message", "Please have another administrator verify your new birthdate before they change it.");
                    return data;
                }
                LocalDate birthdate = LocalDate.parse(value);
                if(birthdate.isAfter(LocalDate.now().minusYears(18))){
                    data.put("success", false);
                    data.put("message", "Age invalid. Users must be at least 18 to register");
                    return data;
                }
                if(birthdate.isBefore(LocalDate.now().minusYears(150))){
                    data.put("success", false);
                    data.put("message", "Age invalid. Please double-check the date. Birthdate cannot be more than 150 years ago");
                    return data;
                }
                try{
                    user.setBirthdate(birthdate);
                    userService.saveUser(user);
                } catch (TransactionSystemException e){
                    data.put("success", false);
                    data.put("message", "Age validation failed.");
                    return data;
                }
        }

        data.put("success", true);
        data.put("message", fieldMsg + " successfully changed");
        return data;
    }

}
