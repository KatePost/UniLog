package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/api/*")
public class ApiController {

    private final ApiService apiService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final PaymentRecordService paymentRecordService;
    private final RegistrationService registrationService;
    private final CourseService courseService;

    @Autowired
    public ApiController(ApiService apiService, CartItemService cartItemService, UserService userService,  PaymentRecordService paymentRecordService, RegistrationService registrationService, CourseService courseService) {
        this.apiService = apiService;
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.paymentRecordService = paymentRecordService;
        this.registrationService = registrationService;
        this.courseService = courseService;
    }

    //searches

    //admin searching for course
    @GetMapping("/allCourses/{courseNumber}")
    public String findCourses(Model model, @PathVariable String courseNumber) {
        model.addAttribute("listCourses", apiService.getCourseByCourseNumberSearch(courseNumber));
        return "fragments/components :: resultsList";
    }

    //admin searching for section
    @GetMapping("/allSections/{courseNumber}")
    public String findSections(Model model, @PathVariable String courseNumber) {
        model.addAttribute("listSections", apiService.getSectionByCourseNumberSearch(courseNumber));
        return "fragments/components :: sectionsList";
    }

    //student searching for available section
    @GetMapping("/availableSection/{courseNumber}")
    public String availableSectionsSearch(Model model, @PathVariable String courseNumber) {
        if (courseNumber.equals("")) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        } else {
            model.addAttribute("listSections", apiService.getAvailableSectionByNumberSearch(courseNumber));
        }
        return "fragments/components :: sectionsList";
    }

    //student searching for available courses
    @GetMapping("/availableCourses/{courseNumber}")
    public String findAvailableCourses(Model model, @PathVariable String courseNumber) {
        if (courseNumber.equals("")) {
            model.addAttribute("listCourses", apiService.getAvailableCourses());
        } else {
            model.addAttribute("listCourses", apiService.getAvailableCourseByNumberSearch(courseNumber));
        }
        return "fragments/components :: resultsList";
    }

    //show all

    //show admin all courses
    @GetMapping("/allCourses/")
    public String getAllCourses(Model model) {
        model.addAttribute("listCourses", apiService.getCourses());
        return "fragments/components :: resultsList";
    }

    //show admin all sections
    @GetMapping("allSections/")
    public String getAllSections(Model model) {
        model.addAttribute("listSections", apiService.getSections());
        return "fragments/components :: sectionsList";
    }

    //show student all courses
    @GetMapping("/availableCourses/")
    public String getAvailableCourses(Model model) {
        model.addAttribute("listCourses", apiService.getAvailableCourses());
        System.out.println(apiService.getAvailableCourses());
        return "fragments/components :: resultsList";
    }

    //show student all sections
    @GetMapping("/availableSection/")
    public String availableSections(Model model) {
        model.addAttribute("listSections", apiService.getAvailableSections());
        System.out.println(apiService.getAvailableSections());
        return "fragments/components :: sectionsList";
    }

    //student display a single course info
    @GetMapping("/student/singleCourse/{courseNumber}")
    public String singleCourseStudent(Model model, @PathVariable String courseNumber) {
        model.addAttribute("singleCourse", apiService.getCourseByNumber(courseNumber));
        return "fragments/studentGeneral :: singleCourse";
    }

    //show student single section
    @GetMapping("/student/singleSection/{id}")
    public String singleSectionStudent(Model model, @PathVariable Long id, Principal principal) {
        Section section = apiService.getSectionById(id);
        User student = userService.findByEmail(principal.getName());

        if(cartItemService.verifyCartItem(section, student)){
            model.addAttribute("available", true);
        }else{
            model.addAttribute("available", false);
        }

        model.addAttribute("singleSection", section);

        return "fragments/studentGeneral :: singleSection";
    }

    //admin toggle disable course
    @GetMapping("/admin/disableCourse/{courseNumber}")
    public String disableCourse(@PathVariable String courseNumber, RedirectAttributes redirectAttributes){
        apiService.toggleDisableCourse(courseNumber);
        System.out.println("success");
        redirectAttributes.addFlashAttribute("message", "Course information has been updated");
        return "redirect:/admin/updateCourse/" + courseNumber;
    }

    //admin toggle disable section
    @GetMapping("/admin/disableSection/{id}")
    public String disableSection(@PathVariable Long id, RedirectAttributes redirectAttributes){
        apiService.toggleDisableSection(id);
        System.out.println("success");
        redirectAttributes.addFlashAttribute("message", "Section information has been updated");
        return "redirect:/admin/updateSection/" + id;
    }

    //student payment history - get list of applicable sections
    @GetMapping("/paidSections/{paymentId}")
    public String paidSectionsList(@PathVariable Long paymentId, Model model) {
        PaymentRecord paymentRecord = paymentRecordService.findById(paymentId);
        List<RegisteredCourse> registeredCourseList = registrationService.getRegisteredCourseByPaymentRecord(paymentRecord);
        List<Section> sectionList = new ArrayList<>();
        for(RegisteredCourse registeredCourse : registeredCourseList){
            sectionList.add(registeredCourse.getSection());
        }
        model.addAttribute("sectionList", sectionList);
        return "fragments/studentGeneral :: sectionList";
    }
}
