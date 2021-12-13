package com.AK.unilog.service;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.CartItemRepo;
import com.AK.unilog.repository.RegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Service
public class RegistrationService {

    private RegistrationRepo registrationRepo;
    private CartItemRepo cartItemRepo;

    @Autowired
    public RegistrationService(RegistrationRepo registrationRepo, CartItemRepo cartItemRepo) {
        this.registrationRepo = registrationRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public RegistrationRepo getRegistrationRepo() {
        return registrationRepo;
    }


    /**
     * This method registers all courses in the cart (set of cartItems)
     * @param cart The user's cart - a list of courses the student wishes to register for
     * @return Messages - a list of success or failure messages with a detailed list of errors encountered during registration.
     */
    public HashMap<String, ArrayList<String>> registerCart(Set<CartItem> cart){
        HashMap<String, ArrayList<String>> messages = new HashMap<>();
        messages.put("confirmations", new ArrayList<>());
        messages.put("errors", new ArrayList<>());
        for(CartItem item : cart){
            //TODO: think about moving this stuff to its own method
            Section section = item.getSection();
            Boolean errors = false;
            /* validation */
            if(section.isDisabled()){
                messages.get("errors").add(section.getCourse().getTitle() + " is not currently available");
                errors = true;
            }
            //TODO: check date validation and due date assignment
            if(section.getStartDate().isBefore(LocalDate.now())){
                messages.get("errors").add(section.getCourse().getTitle() + " cannot be registered in the past");
                errors = true;
            }
            if(section.getSeatsAvailable() < 1){
                messages.get("errors").add(section.getCourse().getTitle() + " is full.");
                errors = true;
            }
            //if there are errors, do not proceed to registration of this course
            //TODO: check prerequisites validation
            if(!item.getStudent().hasPrerequisite(section)){
                messages.get("errors").add(section.getCourse().getTitle() + " - you do not have the prerequisites to take this course.");
            }
            if(errors){
                messages.get("confirmations").add(String.format("Unable to register for %s %s %s. See conflicts.",
                        section.getCourse().getTitle(), section.getSemester().name(), section.getYear()));
                continue;
            }
            RegisteredCourse registration = new RegisteredCourse();
            registration.setSection(section);
            registration.setUser(item.getStudent());
            registration.setDueDate(section.getStartDate().plusMonths(3));
            registrationRepo.save(registration);

            messages.get("confirmations").add(String.format("Successfully registered for %s %s %s",
                    section.getCourse().getTitle(), section.getSemester().name(), section.getYear()));
        }
        cartItemRepo.deleteAllInBatch(cart);
        return messages;
    }


}
