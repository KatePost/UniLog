package com.AK.unilog.service;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.RegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

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


    /**
     * This method registers all courses in the cart (set of cartItems)
     * @param cart The user's cart - a list of courses the student wishes to register for
     * @return Errors - a list of errors for courses that could not be registered
     */
    public ArrayList<String> registerCart(Set<CartItem> cart){
        ArrayList<String> errors = new ArrayList<>();
        for(CartItem item : cart){
            Section section = item.getSection();
            if(section.isDisabled()){
                errors.add(section.getCourse().getTitle() + " is not currently available");
                break; //some sort of error message?
            }
            if(section.getYear() < LocalDate.now().getYear()){
                errors.add(section.getCourse().getTitle() + " cannot be registered in the past");
                break;
            }
            if(section.getSeatsAvailable() < 1){
                errors.add(section.getCourse().getTitle() + " is full.");
                break;
            }
            RegisteredCourse registration = new RegisteredCourse();
            registration.setSection(section);
            registration.setUser(item.getStudent());
            registration.setDueDate(generateDueDate(section));
            registrationRepo.save(registration);
        }
        return errors;
    }

    private static LocalDate generateDueDate(Section section){
        int month = switch (section.getSemester()){
            case WINTER -> 4;
            case SPRING -> 7;
            case SUMMER -> 10;
            case FALL -> 1;
        };

        int year = switch (section.getSemester()){
            case WINTER, SPRING, SUMMER -> section.getYear();
            case FALL -> section.getYear() + 1;
        };

        return LocalDate.of(year, month, 1);
    }

}
