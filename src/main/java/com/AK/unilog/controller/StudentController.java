package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.repository.CartItemRepository;
import com.AK.unilog.repository.PaymentItemRepository;
import com.AK.unilog.repository.RegistrationRepo;
import com.AK.unilog.repository.SectionsRepository;
import com.AK.unilog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

import java.util.Optional;


/*
Note* Must determine a way to specify that all of these urls as /student/{url}
and specify the files as /student/{htmlFileName}
 */

@Controller
@RequestMapping("/student/*")
public class StudentController {

    private final UserService userService;
    private final RegistrationService registrationService;
    private final CourseService courseService;
    private final CartItemService cartItemService;
    private SectionsRepository sectionsRepository;
    private final PaymentItemService paymentItemService;


    @Autowired
    public StudentController(UserService userService, RegistrationService registrationService,
                             CourseService courseService, CartItemService cartItemService, SectionsRepository sectionsRepository, PaymentItemService paymentItemService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.courseService = courseService;
        this.cartItemService = cartItemService;
        this.sectionsRepository = sectionsRepository;
        this.paymentItemService = paymentItemService;
    }



    @GetMapping({"", "home"})
    public String home(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("user", student);
        model.addAttribute("unpaidSum", student.getUnpaidSum());
        return "student/home";
    }

    @GetMapping("availableCourses")
    public String availableCourses(){
        return "student/availableCourses";
    }

    @GetMapping("availableSections")
    public String availableSections(){
        return "student/availableSections";
    }

    @GetMapping("cart")
    public String showCart(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        Set<CartItem>cart = new HashSet<>(student.getCart());
        model.addAttribute("cart", cart);
        //get the subtotal of all the cart items
        double total = 0;
        for(CartItem item : cart){
            total += item.getSection().getCourse().getPrice();
        }
        model.addAttribute("total", total);
        //figure out what the due date is
        //student and section can be passed from cart item.
        //these cart items have to disappear when the post is made
        return "student/cart";
    }

    @PostMapping("checkout")
    public String checkout(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());

        HashMap<String, ArrayList<String>> messages = registrationService.registerCart(student.getCart());
        model.addAttribute("errorMsgs", messages.get("errors"));
        model.addAttribute("confMsgs", messages.get("confirmations"));
        return "student/checkout";
    }

    @PostMapping("paymentConfirmation")
    public String confirmPayment() {
        return "student/paymentConfirmation";
    }

    @PostMapping(value = "editRegistration", params = "action=save")
    public String proceedToPayment(@RequestParam(name = "sectionId", required = false) List<Long> registeredIdList,
                                   @RequestParam(name = "total")String total, Principal principal){
        if(registeredIdList == null){
            return "redirect:/student/registeredCourses";
        }
        List<RegisteredCourse>registeredList = new ArrayList<>();
        for(Long id: registeredIdList){
            registeredList.add(registrationService.getById(id));
        }
        User student = userService.findByEmail(principal.getName());
        paymentItemService.verifyPaymentItems(registeredList, student);
        return "redirect:/payment/makePayment";
    }

    @PostMapping(value = "editRegistration", params = "action=delete")
    public String deleteRegistrations(@RequestParam(name = "sectionId", required = false) List<Long> registeredIdList, RedirectAttributes redirect){
        if(registeredIdList == null){
            return "redirect:/student/registeredCourses";
        }
        StringBuilder message = new StringBuilder("Course registrations deleted: ");
        StringBuilder error = new StringBuilder();
        for(Long id: registeredIdList){
            RegisteredCourse deleted = registrationService.getRegistrationRepo().getById(id);
            if(deleted.getSection().getStartDate().isBefore(LocalDate.now().minusWeeks(2))){
                error.append(String.format("%s %s %s; ", deleted.getSection().getCourse().getCourseNumber(),
                        deleted.getSection().getSemester().name(), deleted.getSection().getYear()));
            } else {
                message.append(String.format("%s %s %s; ", deleted.getSection().getCourse().getCourseNumber(),
                        deleted.getSection().getSemester().name(), deleted.getSection().getYear()));
                registrationService.getRegistrationRepo().deleteById(id);
            }
        }
        redirect.addFlashAttribute("deleteMsg", message.toString());
        if(!error.isEmpty()){
            error.append("Could not be deleted");
            redirect.addFlashAttribute("errorMsg", error.toString());
        }
        return "redirect:/student/registeredCourses";
    }

    @GetMapping("registeredCourses")
    public String registeredCourses(Model model, Principal principal,
                                    @RequestParam(value = "sortBy", required = false)String sortBy,
                                    @RequestParam(value = "filter", required = false)String filter){

        List<RegisteredCourse>registeredCourses = new ArrayList<>(userService.findByEmail(principal.getName()).getRegisteredCourses());
        if(sortBy != null) {
            switch (sortBy) {
                case "startDate" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getStartDate()));
                case "dueDate" -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getDueDate));
                case "datePaid" -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getDueDate)
                        .thenComparing(RegisteredCourse::getPaymentRecord, Comparator.nullsLast(Comparator.comparing(PaymentRecord::getPaymentDate))));

                case "courseCode" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getCourse().getCourseNumber()));
                case "title" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getCourse().getTitle()));
                case "owing" -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getPaymentRecord,
                        Comparator.nullsFirst(Comparator.comparing(PaymentRecord::getTotalPayment)))
                        .thenComparing(RegisteredCourse::getFee)
                        .thenComparing(RegisteredCourse::getDueDate));

            }
        }


//        HashSet<RegisteredCourse> registeredCourses = new HashSet<>(userService.findByEmail(principal.getName()).getRegisteredCourses());
        model.addAttribute("registeredCourses", registeredCourses);
        return "student/registeredCourses";
    }


    @GetMapping("paymentHistory")
    public String paymentHistory(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("student", student);
        return "student/paymentHistory";
    }

    @GetMapping("sections/{courseNumber}")
    public String showSpecificSections(@PathVariable String courseNumber, Model model){
        System.out.println("inside student/sections/courseNumber");
        courseService.getCourseByNumber(courseNumber);
        model.addAttribute("listSections", courseService.getAvailableSectionsByCourse(courseNumber));
        System.out.println(courseService.getAvailableSectionsByCourse(courseNumber));
        return "student/singleCourseSections";
    }

    @GetMapping("addToCart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes){
        Section section = courseService.getSectionById(id);
        User user = userService.findByEmail(principal.getName());
        if(section != null && user != null){
            boolean verifiedCartItem = cartItemService.verifyCartItem(section, user);
            if(verifiedCartItem){
                cartItemService.saveCartItem(section, user);
                redirectAttributes.addFlashAttribute("message", "Section added to course cart successfully.");
                return "redirect:/student/cart";
            }
            redirectAttributes.addFlashAttribute("dangerMessage", "Cannot be added to Cart: This section is either in your Cart, or in your registered courses");
            return "redirect:/student/sections/" + section.getCourse().getCourseNumber();
        }
        redirectAttributes.addFlashAttribute("dangerMessage", "Section cannot be added to cart.");
        return "redirect:/student/availableCourses";
    }

    @GetMapping("studentDetails")
    public String viewDetails(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("user", student);
        return "student/studentDetails";
    }

    @PostMapping(value = "studentDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody() String editDetails(@RequestParam("field")String field, @RequestParam("value")String value, Principal principal){
            User user = userService.findByEmail(principal.getName());
            switch (field) {
                case "firstName":
                    try {
                        user.setFirstName(value);
                        userService.saveUser(user);
                    } catch (TransactionSystemException e) {
                        return """
                                {
                                    "success" : false,
                                    "message" : "First name invalid"
                                }""";
                    }
                    break;
                case "lastName":
                    try {
                        user.setLastName(value);
                        userService.saveUser(user);
                    } catch(TransactionSystemException e){
                        return """
                                {
                                    "success" : false,
                                    "message" : "Last name invalid"
                                }""";
                    }
                    break;
                case "email":
                    if(userService.findByEmail(value) != null){
                        return """
                                {
                                    "success" : false,
                                    "message" : "Email in use"
                                }""";
                    }
                    try {
                        user.setEmail(value);
                        userService.saveUser(user);
                    } catch(TransactionSystemException e){
                        return """
                                {
                                    "success" : false,
                                    "message" : "Email invalid"
                                }""";
                    }
                    break;
                case "address":
                    try {
                        user.setAddress(value);
                        userService.saveUser(user);
                    } catch(TransactionSystemException e){
                        return """
                                {
                                    "success" : false,
                                    "message" : "Address invalid"
                                }""";
                    }
                    break;
            }

        return """
                                {
                                    "success" : true,
                                    "message" : "Field successfully changed"
                                }""";
    }

    @PostMapping("changePassword")
    public String changePassword(RedirectAttributes redirect, @RequestParam("newPassword")String newPassword, @RequestParam("confirmPassword")String confirmPassword, Principal principal){
        if(!newPassword.equals(confirmPassword)){
            redirect.addFlashAttribute("errorMsg", "Passwords do not match");
            return "redirect:/student/studentDetails";
        }
        User user = userService.findByEmail(principal.getName());
        try{
            user.setPassword(newPassword);
            user.setPasswordMatch(confirmPassword);
            userService.saveUser(user, user.getRole());
        } catch (TransactionSystemException e){
            redirect.addFlashAttribute("errorMsg", "Password could not be validated. No change was made");
            return "redirect:/student/studentDetails";
        }
        redirect.addFlashAttribute("message", "New password saved");
        return "redirect:/student/studentDetails";
    }
}
