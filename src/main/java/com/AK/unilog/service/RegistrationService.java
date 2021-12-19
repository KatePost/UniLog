package com.AK.unilog.service;

import com.AK.unilog.entity.*;
import com.AK.unilog.repository.CartItemRepo;
import com.AK.unilog.repository.RegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RegistrationService {

    private final RegistrationRepo registrationRepo;
    private final CartItemRepo cartItemRepo;

    @Autowired
    public RegistrationService(RegistrationRepo registrationRepo, CartItemRepo cartItemRepo) {
        this.registrationRepo = registrationRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public RegistrationRepo repo() {
        return registrationRepo;
    }

    public List<RegisteredCourse> findAll() {
        return registrationRepo.findAll();
    }

    public List<RegisteredCourse> findAll(Sort sort) {
        return registrationRepo.findAll(sort);
    }

    public List<RegisteredCourse> findAllById(Iterable<Long> longs) {
        return registrationRepo.findAllById(longs);
    }

    public List<RegisteredCourse> saveAll(Iterable<RegisteredCourse> entities) {
        return registrationRepo.saveAll(entities);
    }

    public void deleteAllInBatch(Iterable<RegisteredCourse> entities) {
        registrationRepo.deleteAllInBatch(entities);
    }

    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        registrationRepo.deleteAllByIdInBatch(longs);
    }

    public void deleteAllInBatch() {
        registrationRepo.deleteAllInBatch();
    }

    public RegisteredCourse getById(Long id){
        return registrationRepo.getById(id);
    }

    public List<RegisteredCourse> findAll(Example<RegisteredCourse> example) {
        return registrationRepo.findAll(example);
    }

    public List<RegisteredCourse> findAll(Example<RegisteredCourse> example, Sort sort) {
        return registrationRepo.findAll(example, sort);
    }

    public void deleteById(Long id){
        registrationRepo.deleteById(id);
    }

    public List<RegisteredCourse>findByUser(User user){
        return registrationRepo.findByUser(user).orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> getRegisteredCourseByPaymentRecord(PaymentRecord paymentRecord) {
        Optional<List<RegisteredCourse>> registeredCourseList = registrationRepo.findByPaymentRecord(paymentRecord);
        return registeredCourseList.orElse(null);
    }

    public List<RegisteredCourse> findUnpaidByUser(User user) {
        return registrationRepo.findUnpaidByUser(user).orElse(new ArrayList<>());
    }
    public List<RegisteredCourse> findPaidByUser(User user) {
        return registrationRepo.findPaidByUser(user).orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findPastByUser(User user) {
        return registrationRepo.findPastByUser(user).orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findUpcomingByUser(User user) {
        return registrationRepo.findUpcomingByUser(user).orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findUnpaid() {
        return registrationRepo.findUnpaid().orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findPaid() {
        return registrationRepo.findPaid().orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findPast() {
        return registrationRepo.findPast().orElse(new ArrayList<>());
    }

    public List<RegisteredCourse> findUpcoming() {
        return registrationRepo.findUpcoming().orElse(new ArrayList<>());
    }



    /**
     * This method registers all courses in the cart (set of cartItems)
     * @param cart The user's cart - a list of courses the student wishes to register for
     * @return Messages - a list of success or failure messages with a detailed list of errors encountered during registration.
     */
    public HashMap<String, ArrayList<String>> registerCart(Iterable<CartItem> cart){
        HashMap<String, ArrayList<String>> messages = new HashMap<>();
        messages.put("confirmations", new ArrayList<>());
        messages.put("errors", new ArrayList<>());
        for(CartItem item : cart){
            //TODO: think about moving this stuff to its own method
            Section section = item.getSection();
            boolean errors = false;
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
            //fixing seats available
            if(section.getSeatsAvailable() < registrationRepo.findBySection(section).get().size()){
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

    public List<RegisteredCourse> getStudentsBySection(Section section) {
        Optional<List<RegisteredCourse>> registeredCourseList = registrationRepo.findBySection(section);
        return registeredCourseList.orElse(null);
    }
}
