package com.AK.unilog.utils;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.RegistrationRepo;
import com.AK.unilog.service.CartItemService;
import com.AK.unilog.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Users {

    private static RegistrationService registrationService;

    @Autowired
    public Users(RegistrationService registrationService){
        Users.registrationService = registrationService;
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

}
