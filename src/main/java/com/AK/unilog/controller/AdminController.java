package com.AK.unilog.controller;

import com.AK.unilog.model.CourseFormModel;
import com.AK.unilog.model.CourseUpdateFormModel;
import com.AK.unilog.model.SectionFormModel;
import com.AK.unilog.model.SectionUpdateFormModel;
import com.AK.unilog.service.CourseService;
import com.AK.unilog.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    private final CourseService courseService;
    private final RegistrationService registrationService;

    @Autowired
    public AdminController(CourseService courseService, RegistrationService registrationService){
        this.courseService = courseService;
        this.registrationService = registrationService;
    }

    @GetMapping("home")
    public String home(){
        return "admin/home";
    }

    @GetMapping("courses")
    public String showCourses(Model model){
        System.out.println("get courses");
        return "admin/courses";
    }

    @GetMapping("sections")
    public String showSections(Model model){
        System.out.println("get sections");
        return "admin/sections";
    }

    @GetMapping("/updateCourse/{courseNumber}")
    public String updateCourse(Model model, @PathVariable String courseNumber){
        if(courseService.getCourseByNumber(courseNumber) == null){
//            return error page TODO
        }
        model.addAttribute("singleCourse", courseService.getCourseByNumber(courseNumber));
        CourseUpdateFormModel courseUpdateFormModel = new CourseUpdateFormModel();
        courseUpdateFormModel.setCourseNumber(courseNumber);
        model.addAttribute("courseUpdateFormModel", courseUpdateFormModel);
        return "admin/updateCourse";
    }

    @GetMapping("/updateSection/{id}")
    public String updateSection(Model model, @PathVariable Long id){
        if(courseService.getSectionById(id) == null){
//            return error page TODO
        }
        model.addAttribute("singleSection", courseService.getSectionById(id));
        SectionUpdateFormModel sectionUpdateFormModel = new SectionUpdateFormModel();
        sectionUpdateFormModel.setId(id);
        model.addAttribute("sectionUpdateFormModel", sectionUpdateFormModel);
        return "admin/updateSection";
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


    @GetMapping("/singleCourse/{courseNumber}")
    public String singleCourseAdmin(Model model, @PathVariable String courseNumber) {
        model.addAttribute("singleCourse", courseService.getCourseByNumber(courseNumber));
        model.addAttribute("courseUpdateFormModel", new CourseUpdateFormModel());
        return "fragments/adminGeneral :: singleCourse";
    }

    @GetMapping("/singleSection/{id}")
    public String singleSectionAdmin(Model model, @PathVariable Long id) {
        System.out.println("inside singlesection controller");
        model.addAttribute("singleSection", courseService.getSectionById(id));
        model.addAttribute("sectionUpdateFormModel", new SectionUpdateFormModel());
        return "fragments/adminGeneral :: singleSection";
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

    @PostMapping("courses")
    public String updateCourseForm(Model model, @Valid CourseUpdateFormModel courseUpdateFormModel, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        System.out.println("post update" + courseUpdateFormModel.getCourseNumber());
        if(bindingResult.hasErrors()){
            model.addAttribute("singleCourse", courseService.getCourseByNumber(courseUpdateFormModel.getCourseNumber()));
            return "/admin/updateCourse";
        }
        System.out.println(courseUpdateFormModel.getCourseNumber());
        model.addAttribute(courseService.saveCourse(courseUpdateFormModel));
        redirectAttributes.addFlashAttribute("message", "Course information has been updated");
        return "redirect:/admin/updateCourse/" + courseUpdateFormModel.getCourseNumber();
    }

    @PostMapping("sections")
    public String updateSectionForm(Model model, @Valid SectionUpdateFormModel sectionUpdateFormModel, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        System.out.println("post update" + sectionUpdateFormModel.getId());
        if(bindingResult.hasErrors()){
            model.addAttribute("singleSection", courseService.getSectionById(sectionUpdateFormModel.getId()));
            return "/admin/updateSection";
        }
        System.out.println(sectionUpdateFormModel.getId());
        model.addAttribute(courseService.saveSection(sectionUpdateFormModel));
        redirectAttributes.addFlashAttribute("message", "Section information has been updated");
        return "redirect:/admin/updateSection/" + sectionUpdateFormModel.getId();
    }

}
