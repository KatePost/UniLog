package com.AK.unilog.controller;

import com.AK.unilog.model.CourseUpdateFormModel;
import com.AK.unilog.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path = "/api/*")
public class ApiController {

    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
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

    @GetMapping("/student/singleCourse/{courseNumber}")
    public String singleCourseStudent(Model model, @PathVariable String courseNumber) {
        model.addAttribute("singleCourse", apiService.getCourseByNumber(courseNumber));
        return "fragments/studentGeneral :: singleCourse";
    }

    @PostMapping(name = "/admin/disableCourse")
    public String disableCourse(@RequestBody String courseNumber){
        System.out.println(courseNumber);
        apiService.toggleDisableCourse(courseNumber);
        System.out.println("success");
        return "redirect:/";
    }

    //show students the available sections
    @GetMapping("/availableSection/{courseNumber}")
    public String availableSectionsSearch(Model model, @PathVariable String courseNumber) {
        System.out.println("in /availableSection/{courseNumber} ");
        if (courseNumber.equals("")) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        } else {
            courseNumber = "^[A-Z0-9]*" + courseNumber + "[A-Z0-9]*$";
            model.addAttribute("listSections", apiService.getAvailableSectionsByCourse(courseNumber));
        }

        return "fragments/studentGeneral :: singleSection";
    }

    @GetMapping("/availableSection/")
    public String availableSections(Model model) {
            model.addAttribute("listSections", apiService.getAvailableSections());
        return "fragments/components :: sectionsList";
    }

    @GetMapping("/student/singleSection/{id}")
    public String singleSectionStudent(Model model, @PathVariable Long id) {
        model.addAttribute("singleSection", apiService.getSectionById(id));
        System.out.println(apiService.getSectionById(id));
        return "fragments/studentGeneral :: singleSection";
    }
}
