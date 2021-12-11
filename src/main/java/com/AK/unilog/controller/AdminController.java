package com.AK.unilog.controller;

import com.AK.unilog.entity.Section;
import com.AK.unilog.model.CourseFormModel;
import com.AK.unilog.model.SectionFormModel;
import com.AK.unilog.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.zip.CheckedOutputStream;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    private CourseService courseService;

    @Autowired
    public AdminController(CourseService courseService){
        this.courseService = courseService;
    }

    @GetMapping("home")
    public String home(){
        return "admin/home";
    }

    @GetMapping("courses")
    public String showCourses(){
        return "admin/courses";
    }

    @GetMapping("newCourse")
    public String newCoursePage(Model model){
        model.addAttribute("courseFormModel", new CourseFormModel());
        return "admin/newCourse";
    }

    @GetMapping("newSection")
    public String newSectionPage(Model model){
        model.addAttribute("sectionFormModel", new SectionFormModel());
        return "admin/newSection";
    }

    @GetMapping("registrations")
    public String showRegistrations(){
        return "admin/registrations";
    }

    @GetMapping("register")
    public String register(){
        return "admin/register";
    }

    @GetMapping("outstanding")
    public String showOutstandingFees(){
        return "admin/outstanding";
    }

    @PostMapping("newSection")
    public String newSectionForm(@Valid SectionFormModel section, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "admin/newSection";
        }
        courseService.verifySection(section, bindingResult);
        if(bindingResult.hasErrors()){
            return "admin/newSection";
        }
        courseService.saveSection(section);
        redirectAttributes.addAttribute("message", "New Section added successfully");
        return "redirect:/admin/home";
    }

    @PostMapping("newCourse")
    public String newSectionForm(@Valid CourseFormModel course, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "/admin/newCourse";
        }
        courseService.verifyCourse(course, bindingResult);
        if(bindingResult.hasErrors()){
            return "/admin/newCourse";
        }
        courseService.saveCourse(course);
        redirectAttributes.addAttribute("message", "New Course added successfully");
        return "redirect:/admin/home";
    }
}
