package com.AK.unilog.controller;

import com.AK.unilog.entity.PaymentRecord;
import com.AK.unilog.entity.RegisteredCourse;
import com.AK.unilog.entity.User;
import com.AK.unilog.model.CourseFormModel;
import com.AK.unilog.model.CourseUpdateFormModel;
import com.AK.unilog.model.SectionFormModel;
import com.AK.unilog.model.SectionUpdateFormModel;
import com.AK.unilog.service.CourseService;
import com.AK.unilog.service.RegistrationService;
import com.AK.unilog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

import static com.AK.unilog.utils.Users.getStringObjectMap;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    private final CourseService courseService;
    private final RegistrationService registrationService;
    private final UserService userService;

    @Autowired
    public AdminController(CourseService courseService, RegistrationService registrationService, UserService userService){
        this.courseService = courseService;
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @GetMapping({"", "home"})
    public String home(){
        return "redirect:/admin/courses";
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
        model.addAttribute("singleCourse", courseService.getCourseByNumber(courseNumber));
        CourseUpdateFormModel courseUpdateFormModel = new CourseUpdateFormModel();
        courseUpdateFormModel.setCourseNumber(courseNumber);
        model.addAttribute("courseUpdateFormModel", courseUpdateFormModel);
        return "admin/updateCourse";
    }

    @GetMapping("/updateSection/{id}")
    public String updateSection(Model model, @PathVariable Long id){
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
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/newSection";
    }

    @GetMapping({"courseRegistrations/{filter}","courseRegistrations"})
    public String showRegistrations(Model model,
                                    @RequestParam(value = "reg", required = false)Long id,
                                    @PathVariable(value = "filter", required = false)String filter,
                                    @RequestParam(value = "sortBy", required = false)String sortBy){
        if(filter == null){
            filter = "all";
        }
        List<RegisteredCourse>registeredCourses = switch(filter){
            case "unpaid" -> new ArrayList<>(registrationService.findUnpaid());
            case "paid" -> new ArrayList<>(registrationService.findPaid());
            case "past" -> new ArrayList<>(registrationService.findPast());
            case "upcoming" -> new ArrayList<>(registrationService.findUpcoming());
            default -> new ArrayList<>(registrationService.findAll());
        };

        if(sortBy!=null) {
            switch (sortBy) {
                case "startDate" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getStartDate()));
                case "dueDate" -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getDueDate));
                case "datePaid" -> registeredCourses.sort(
                        Comparator.comparing(RegisteredCourse::getPaymentRecord, Comparator.nullsLast(Comparator.comparing(PaymentRecord::getPaymentDate)))
                                .thenComparing(RegisteredCourse::getDueDate));
                case "courseCode" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getCourse().getCourseNumber()));
                case "title" -> registeredCourses.sort(Comparator.comparing(r -> r.getSection().getCourse().getTitle()));
                case "owing" -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getPaymentRecord,
                                Comparator.nullsFirst(Comparator.comparing(PaymentRecord::getTotalPayment)))
                        .thenComparing(RegisteredCourse::getFee, Comparator.reverseOrder())
                        .thenComparing(RegisteredCourse::getDueDate));
                case "student" -> registeredCourses.sort(Comparator.comparing(r -> r.getUser().getLastName()));
                default -> registeredCourses.sort(Comparator.comparing(RegisteredCourse::getId));

            }
        }
        model.addAttribute("registeredCourses", registeredCourses);
        if(id != null) {
            RegisteredCourse registeredCourse = registrationService.getById(id);
            model.addAttribute("singleCourse", registeredCourse);
        }
        return "admin/courseRegistrations";
    }

    @PostMapping("deleteRegistration")
    public String deleteRegistrations(@RequestParam(name = "sectionId", required = false) List<Long> sectionIdList, RedirectAttributes redirect) {
        if(sectionIdList == null){
            return "redirect:/admin/courseRegistrations";
        }
        StringBuilder message = new StringBuilder("Course registrations deleted: ");
        for (Long id : sectionIdList) {
            RegisteredCourse deleted = registrationService.getById(id);
            message.append(String.format("%s - %s %s %s; ", deleted.getUser().getLastName(), deleted.getSection().getCourse().getCourseNumber(),
                    deleted.getSection().getSemester().name(), deleted.getSection().getYear()));
            registrationService.deleteById(id);
        }
        redirect.addFlashAttribute("deleteMsg", message.toString());
        return "redirect:/admin/courseRegistrations";
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


    @GetMapping("adminDetails")
    public String viewDetails(Model model, Principal principal){
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("user", student);
        return "admin/adminDetails";
    }

    @PostMapping("adminDetails")
    public @ResponseBody()
    Map<String, Object> editDetails(@RequestParam("field")String field, @RequestParam("value")String value, Principal principal){
        return getStringObjectMap(field, value, principal.getName(), true);
    }

//    static Map<String, Object> getStringObjectMap(@RequestParam("field") String field, @RequestParam("value") String value, String userEmail, UserService userService) {
//        User user = userService.findByEmail(userEmail);
//        Map<String, Object> data = new HashMap<>();
//        String fieldMsg = "";
//
//        switch (field) {
//            case "firstName":
//                try {
//                    user.setFirstName(value);
//                    userService.saveUser(user);
//                } catch (TransactionSystemException e) {
//                    data.put("success", false);
//                    data.put("message", "First name must be between 2 and 200 characters");
//                    return data;
//                }
//                fieldMsg = "First name";
//                break;
//            case "lastName":
//                try {
//                    user.setLastName(value);
//                    userService.saveUser(user);
//                } catch(TransactionSystemException e){
//                    data.put("success", false);
//                    data.put("message", "Last name must be between 2 and 200 characters");
//                    return data;
//                }
//                fieldMsg = "Last name";
//                break;
//            case "address":
//                try {
//                    user.setAddress(value);
//                    userService.saveUser(user);
//                } catch(TransactionSystemException e){
//                    data.put("success", false);
//                    data.put("message", "Address must be a valid address format");
//                    return data;
//                }
//                fieldMsg = "Address";
//                break;
//        }
//
//        data.put("success", true);
//        data.put("message", fieldMsg + " successfully changed");
//        return data;
//    }


    @PostMapping("changePassword")
    public String changePassword(RedirectAttributes redirect, @RequestParam("newPassword")String newPassword, @RequestParam("confirmPassword")String confirmPassword, Principal principal){
        if(!newPassword.equals(confirmPassword)){
            redirect.addFlashAttribute("errorMsg", "Passwords do not match");
            return "redirect:/admin/adminDetails";
        }
        User user = userService.findByEmail(principal.getName());
        try{
            user.setPassword(newPassword);
            user.setPasswordMatch(confirmPassword);
            userService.saveUser(user, user.getRole());
        } catch (TransactionSystemException e){
            redirect.addFlashAttribute("errorMsg", "Password could not be validated. No change was made");
            return "redirect:/admin/adminDetails";
        }
        redirect.addFlashAttribute("message", "New password saved");
        return "redirect:/admin/adminDetails";
    }

    @PostMapping("changeUserPassword/{id}")
    public String changePassword(RedirectAttributes redirect, @RequestParam("newPassword")String newPassword, @RequestParam("confirmPassword")String confirmPassword, @PathVariable("id")long id){
        if(!newPassword.equals(confirmPassword)){
            redirect.addFlashAttribute("errorMsg", "Passwords do not match");
            return "redirect:/admin/editUser/" + id;
        }
        User user = userService.getById(id);
        try{
            user.setPassword(newPassword);
            user.setPasswordMatch(confirmPassword);
            userService.saveUser(user, user.getRole());
        } catch (TransactionSystemException e){
            redirect.addFlashAttribute("errorMsg", "Password could not be validated. No change was made");
            return "redirect:/admin/editUser/" + id;
        }
        redirect.addFlashAttribute("message", "New password saved");
        return "redirect:/admin/editUser/" + id;
    }

    @GetMapping("userList")
    public String viewUsers(Model model, @RequestParam(value = "sortBy", required = false)String sortBy){
        if(sortBy == null){
            sortBy = "id";
        }
        model.addAttribute("userList", userService.findAll(Sort.by(sortBy)));
        return "admin/userList";
    }

    @GetMapping("editUser/{id}")
    public String editUser(Model model, @PathVariable("id")long id){
        model.addAttribute("user", userService.getById(id));
        return "admin/editUser";
    }

    @PostMapping("editUser/{id}")
    public @ResponseBody Map<String, Object> editUser(@PathVariable("id")long id, @RequestParam("field")String field, @RequestParam("value")String value){
        User user = userService.getById(id);
        return getStringObjectMap(field, value, user.getEmail(), false);
    }
}
