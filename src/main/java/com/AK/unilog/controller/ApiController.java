package com.AK.unilog.controller;

import com.AK.unilog.entity.*;
import com.AK.unilog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @Autowired
    public ApiController(ApiService apiService, CartItemService cartItemService, UserService userService,  PaymentRecordService paymentRecordService, RegistrationService registrationService) {
        this.apiService = apiService;
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.paymentRecordService = paymentRecordService;
        this.registrationService = registrationService;
    }

    //searches

    //admin searching for course
    @GetMapping("/allCourses")
    public String findCourses(Model model, @RequestParam(value = "searchVal", required = false)String courseNumber, @RequestParam(value = "sortBy", required = false)String sortBy) {
        System.out.println(sortBy);
        System.out.println(courseNumber);
        if(sortBy == null){
            sortBy = "courseNumber";
        }
        if(courseNumber == null){
            courseNumber = "";
        }
        model.addAttribute("listCourses", apiService.getCourseByCourseNumberSearch(courseNumber, Sort.by(sortBy)));
        return "fragments/components :: resultsList";
    }

    //admin searching for section
    @GetMapping("/allSections")
    public String findSections(Model model, @RequestParam(value = "searchVal", required = false)String courseNumber, @RequestParam(value = "sortBy", required = false)String sortBy) {
        System.out.println(sortBy);
        if(sortBy == null){
            sortBy = "courseNumber";
        }
        if(courseNumber == null){
            courseNumber = "";
        }
        model.addAttribute("listSections", apiService.getSectionByCourseNumberSearch(courseNumber, Sort.by(sortBy)));
        return "fragments/components :: sectionsList";
    }

    //student searching for available section
    @GetMapping("/availableSection/{courseNumber}")
    public String availableSectionsSearch(Model model, @PathVariable String courseNumber, @RequestParam(name = "sortBy", required = false)String sortBy) {
        if (courseNumber.equals("")) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        } else {
            model.addAttribute("listSections", apiService.getAvailableSectionByNumberSearch(courseNumber, Sort.by(sortBy)));
        }
        return "fragments/components :: sectionsList";
    }

    //student searching for available courses
    @GetMapping("/availableCourses/{courseNumber}")
    public String findAvailableCourses(Model model, @PathVariable String courseNumber, @RequestParam(name = "sortBy", required = false)String sortBy) {
        System.out.println(sortBy);
        if(sortBy == null){
            sortBy = "courseNumber";
        }
        if (courseNumber.equals("")) {
            model.addAttribute("listCourses", apiService.getAvailableCourses());
        } else {
            model.addAttribute("listCourses", apiService.getAvailableCourseByNumberSearch(courseNumber, Sort.by(sortBy)));
        }
        return "fragments/components :: resultsList";
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
