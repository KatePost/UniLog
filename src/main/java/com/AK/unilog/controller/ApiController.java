package com.AK.unilog.controller;

import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.model.CourseUpdateFormModel;
import com.AK.unilog.service.ApiService;
import com.AK.unilog.service.CartItemService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping(path = "/api/*")
public class ApiController {

    private final ApiService apiService;
    private final CartItemService cartItemService;
    private final UserService userService;

    @Autowired
    public ApiController(ApiService apiService, CartItemService cartItemService, UserService userService) {
        this.apiService = apiService;
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @GetMapping("/allCourses/{courseNumber}")
    public String findCourses(Model model, @PathVariable String courseNumber) {
        System.out.println("here");
        if (courseNumber.equals("")) {
            model.addAttribute("listCourses", apiService.getCourses());
        } else {
            courseNumber = "^[A-Z0-9]*" + courseNumber + "[A-Z0-9]*$";
            model.addAttribute("listCourses", apiService.getCourseByNumberRegex(courseNumber));
        }
        return "fragments/components :: resultsList";
    }

    @GetMapping("/allCourses/")
    public String getAllCourses(Model model) {
        model.addAttribute("listCourses", apiService.getCourses());
        return "fragments/components :: resultsList";
    }

    @GetMapping("allSections/")
    public String getAllSections(Model model) {
        System.out.println("in all sections");
        model.addAttribute("listSections", apiService.getSections());
        System.out.println(apiService.getSections());
        return "fragments/components :: sectionsList";
    }

    @GetMapping("/allSections/{courseNumber}")
    public String findSections(Model model, @PathVariable String courseNumber) {
        System.out.println("here");
        if (courseNumber.equals("")) {
            model.addAttribute("listSections", apiService.getCourses());
        } else {
            courseNumber = "^[A-Z0-9]*" + courseNumber + "[A-Z0-9]*$";
            model.addAttribute("listSections", apiService.getCourseByNumberRegex(courseNumber));
        }
        return "fragments/components :: sectionsList";
    }

    @GetMapping("/availableCourses/")
    public String getAvailableCourses(Model model) {
        model.addAttribute("listCourses", apiService.getAvailableCourses());
        System.out.println(apiService.getAvailableCourses());
        return "fragments/components :: resultsList";
    }

    @GetMapping("/availableCourses/{courseNumber}")
    public String findAvailableCourses(Model model, @PathVariable String courseNumber) {
        if (courseNumber.equals("")) {
            model.addAttribute("listCourses", apiService.getAvailableCourses());
        } else {
            courseNumber = "^[A-Z0-9]*" + courseNumber + "[A-Z0-9]*$";
            model.addAttribute("listCourses", apiService.getAvailableCourseByNumberRegex(courseNumber));
        }
        return "fragments/components :: resultsList";
    }

    @GetMapping("/student/singleCourse/{courseNumber}")
    public String singleCourseStudent(Model model, @PathVariable String courseNumber) {
        model.addAttribute("singleCourse", apiService.getCourseByNumber(courseNumber));
        return "fragments/studentGeneral :: singleCourse";
    }

    @GetMapping("/admin/disableCourse/{courseNumber}")
    public String disableCourse(@PathVariable String courseNumber, RedirectAttributes redirectAttributes){
        apiService.toggleDisableCourse(courseNumber);
        System.out.println("success");
        redirectAttributes.addFlashAttribute("message", "Course information has been updated");
        return "redirect:/admin/updateCourse/" + courseNumber;
    }

    @GetMapping("/admin/disableSection/{id}")
    public String disableSection(@PathVariable Long id, RedirectAttributes redirectAttributes){
        apiService.toggleDisableSection(id);
        System.out.println("success");
        redirectAttributes.addFlashAttribute("message", "Section information has been updated");
        return "redirect:/admin/updateSection/" + id;
    }

    //show students the available sections
    @GetMapping("/availableSection/{courseNumber}")
    public String availableSectionsSearch(Model model, @PathVariable String courseNumber) {
        System.out.println("in /availableSection/{courseNumber} ");
        if (courseNumber.equals("")) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        } else {
            courseNumber = "^[A-Z0-9]*" + courseNumber + "[A-Z0-9]*$";
            model.addAttribute("listSections", apiService.getSectionByNumberRegex(courseNumber));
            System.out.println(apiService.getAvailableSectionsByCourse(courseNumber));
        }

        return "fragments/components :: sectionsList";
    }

    @GetMapping("/availableSection/")
    public String availableSections(Model model) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        System.out.println(apiService.getAvailableSections());
        return "fragments/components :: sectionsList";
    }

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
}
