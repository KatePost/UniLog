package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.repository.SectionsRepository;
import com.AK.unilog.service.*;
import com.AK.unilog.utils.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;


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
    private final PaymentItemService paymentItemService;
    private final PaymentRecordService paymentRecordService;

    @Autowired
    public StudentController(UserService userService, RegistrationService registrationService,
                             CourseService courseService, CartItemService cartItemService, SectionsRepository sectionsRepository, PaymentItemService paymentItemService,
                             PaymentRecordService paymentRecordService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.courseService = courseService;
        this.cartItemService = cartItemService;
        this.paymentItemService = paymentItemService;
        this.paymentRecordService = paymentRecordService;
    }

    @GetMapping({"", "home"})
    public String home(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("user", student);
        model.addAttribute("unpaidSum", Users.getUnpaidSum(student));
//        model.addAttribute("unpaidSum", student.getUnpaidSum());
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
        Set<CartItem>cart = new HashSet<>(cartItemService.findAllByStudent(student));//student.getCart()
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

        HashMap<String, ArrayList<String>> messages = registrationService.registerCart(cartItemService.findAllByStudent(student));//student.getCart()
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
            RegisteredCourse deleted = registrationService.getById(id);
            if(deleted.getSection().getStartDate().isBefore(LocalDate.now().minusWeeks(2))){
                error.append(String.format("%s %s %s; ", deleted.getSection().getCourse().getCourseNumber(),
                        deleted.getSection().getSemester().name(), deleted.getSection().getYear()));
            } else {
                message.append(String.format("%s %s %s; ", deleted.getSection().getCourse().getCourseNumber(),
                        deleted.getSection().getSemester().name(), deleted.getSection().getYear()));
                registrationService.deleteById(id);
            }
        }
        redirect.addFlashAttribute("deleteMsg", message.toString());
        if(!error.isEmpty()){
            error.append("Could not be deleted");
            redirect.addFlashAttribute("errorMsg", error.toString());
        }
        return "redirect:/student/registeredCourses";
    }

    @GetMapping({"registeredCourses", "registeredCourses/{filter}"})
    public String registeredCourses(Model model, Principal principal,
                                    @RequestParam(value = "sortBy", required = false)String sortBy,
                                    @PathVariable(value = "filter", required = false)String filter){
        if(filter == null){
            filter = "all";
        }
        List<RegisteredCourse>registeredCourses = switch (filter){
            case "unpaid" -> new ArrayList<>(registrationService.findUnpaidByUser(userService.findByEmail(principal.getName())));
            case "paid" -> new ArrayList<>(registrationService.findPaidByUser(userService.findByEmail(principal.getName())));
            case "past" -> new ArrayList<>(registrationService.findPastByUser(userService.findByEmail(principal.getName())));
            case "upcoming" -> new ArrayList<>(registrationService.findUpcomingByUser(userService.findByEmail(principal.getName())));
            default -> new ArrayList<>(registrationService.findByUser(userService.findByEmail(principal.getName())));
        };

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
                        .thenComparing(RegisteredCourse::getFee, Comparator.reverseOrder())
                        .thenComparing(RegisteredCourse::getDueDate));
                default -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getId));
            }
        }



//        HashSet<RegisteredCourse> registeredCourses = new HashSet<>(userService.findByEmail(principal.getName()).getRegisteredCourses());
        model.addAttribute("registeredCourses", registeredCourses);
        return "student/registeredCourses";
    }


    @GetMapping("paymentHistory")
    public String paymentHistory(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        List<PaymentRecord> paymentRecords = paymentRecordService.findRecordsByStudent(student);
        model.addAttribute("paymentRecords", paymentRecords);
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

    @PostMapping("studentDetails")
    public @ResponseBody() Map<String, Object> editDetails(@RequestParam("field")String field, @RequestParam("value")String value, Principal principal){
        User user = userService.findByEmail(principal.getName());
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
//            case "email":
//                if(userService.findByEmail(value) != null){
//                    data.put("success", false);
//                    data.put("message", "Email address is already in use");
//                    return data;
//                }
//                try {
//                    user.setEmail(value);
//                    userService.saveUser(user);
//                } catch(TransactionSystemException e){
//                    data.put("success", false);
//                    data.put("message", "Email must be a valid email address format of no more than 150 characters");
//                    return data;
//                }
//                fieldMsg = "Email address";
//                break;
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
        }

        data.put("success", true);
        data.put("message", fieldMsg + " successfully changed");
        return data;
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
