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
}
