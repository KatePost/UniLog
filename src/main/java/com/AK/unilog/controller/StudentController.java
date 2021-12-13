package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.repository.CartItemRepository;
import com.AK.unilog.repository.SectionsRepository;
import com.AK.unilog.service.CartItemService;
import com.AK.unilog.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import com.AK.unilog.service.RegistrationService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import java.util.Optional;


/*
Note* Must determine a way to specify that all of these urls as /student/{url}
and specify the files as /student/{htmlFileName}
 */

@Controller
@RequestMapping("/student/*")
public class StudentController {

    private UserService userService;
    private RegistrationService registrationService;
    private final CourseService courseService;
    private final CartItemService cartItemService;
    private SectionsRepository sectionsRepository;


    @Autowired
    public StudentController(UserService userService, RegistrationService registrationService,
                             CourseService courseService, CartItemService cartItemService, SectionsRepository sectionsRepository) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.courseService = courseService;
        this.cartItemService = cartItemService;
        this.sectionsRepository = sectionsRepository;
    }



    @GetMapping("home")
    public String home(){
        return "student/home";
    }

    @GetMapping("availableCourses")
    public String availableCourses(){
        return "student/availableCourses";
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

    @PostMapping("makePayment")
    public String proceedToPayment(@RequestParam(name = "sectionId") List<String> sectionIdList,
                                   @RequestParam(name = "total")String total){
        List<Section>sectionList = new ArrayList<>();
        for(String id: sectionIdList){
            sectionList.add(sectionsRepository.getById(Long.parseLong(id)));
        }
        return "makePayment";
    }

    @GetMapping("registeredCourses")
    public String registeredCourses(Model model, Principal principal){
        HashSet<RegisteredCourse> registeredCourses = new HashSet<>(userService.findByEmail(principal.getName()).getRegisteredCourses());
        model.addAttribute("registeredCourses", registeredCourses);
        return "student/registeredCourses";
    }

    @GetMapping("paymentHistory")
    public String paymentHistory(){
        return "student/paymentHistory";
    }

    @GetMapping("sections/{courseNumber}")
    public String showSpecificSections(@PathVariable String courseNumber, Model model){
        System.out.println("inside student/sections/courseNumber");
        courseService.getCourseByNumber(courseNumber);
        model.addAttribute("section", courseService.getSectionsByCourseNumber(courseNumber));
        System.out.println(courseService.getSectionsByCourseNumber(courseNumber));
        return "student/availableSections";
    }

    @GetMapping("addToCart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes){
        Section section = courseService.getSectionById(id);
        User user = userService.findByEmail(principal.getName());
        if(section != null && user != null){
            CartItem cartItem = cartItemService.verifyCartItem(section, user);
            if(cartItem != null){
                redirectAttributes.addFlashAttribute("message", "Section added to course cart successfully.");
                return "redirect:/home";
            }
            redirectAttributes.addFlashAttribute("message", "Section cannot be added to cart.");
            return "redirect:/student/sections/" + section.getCourse().getCourseNumber();
        }
        redirectAttributes.addFlashAttribute("message", "Section cannot be added to cart.");
        return "redirect:/student/availableCourses";
    }
}
