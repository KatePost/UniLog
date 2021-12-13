package com.AK.unilog.service;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.Section;
import com.AK.unilog.model.CourseFormModel;
import com.AK.unilog.model.CourseUpdateFormModel;
import com.AK.unilog.model.SectionFormModel;
import com.AK.unilog.model.SectionUpdateFormModel;
import com.AK.unilog.repository.CourseRepository;
import com.AK.unilog.repository.SectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final SectionsRepository sectionsRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, SectionsRepository sectionsRepository){
        this.courseRepository = courseRepository;
        this.sectionsRepository = sectionsRepository;
    }

    public void verifySection(SectionFormModel section, BindingResult bindingResult) {
        Optional<Course> course = courseRepository.findByCourseNumber(section.getCourseNumber());
        if (course.isEmpty()) {
            bindingResult.rejectValue("courseNumber", "not.found", "No existing course matches the course number");
            return;
        }
        Semester sem;
        try {
            sem = Semester.valueOf(section.getSemester());
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("semester", "not.valid", "The semester is not valid");
            return;
        }
        Optional<Section> existingSection = sectionsRepository.findByCourseAndYearAndSemester(course.get(), section.getYear(), sem);
        if(existingSection.isPresent()){
            bindingResult.rejectValue("courseNumber", "already.exists", "This course already has a section for the given semester");
        }
    }

    public Section saveSection(SectionFormModel sectionFormModel){
        Section section = new Section();
        section.setCourse(courseRepository.findByCourseNumber(sectionFormModel.getCourseNumber()).get());
        section.setYear(sectionFormModel.getYear());
        section.setSemester(Semester.valueOf(sectionFormModel.getSemester()));
        section.setSeatsAvailable(sectionFormModel.getSeatsAvailable());

        return sectionsRepository.save(section);
    }

    public void verifyCourse(CourseFormModel course, BindingResult bindingResult) {
        if(!course.getCourseNumber().matches("^[A-Z]{4}[0-9]{3}$")){
            bindingResult.rejectValue("courseNumber", "not.valid", "Course Number must be in format ABCD123");
            return;
        }
        if(courseRepository.findByCourseNumber(course.getCourseNumber()).isPresent()){
            bindingResult.rejectValue("courseNumber", "not.found", "That Course number already exists");
            return;
        }
        if(course.getPrereq().length() > 0){
            if(courseRepository.findByCourseNumber(course.getPrereq()).isEmpty()){
                bindingResult.rejectValue("prereq", "not.found", "The prerequisite course number entered doesn't match any Courses");
            }
        }
    }

    public Course saveCourse(CourseFormModel courseFormModel) {
        Course course = new Course();
        course.setCourseNumber(courseFormModel.getCourseNumber());
        course.setTitle(courseFormModel.getTitle());
        course.setDescription(courseFormModel.getDescription());
        course.setPrice(courseFormModel.getPrice());

        Optional<Course> prereq = courseRepository.findByCourseNumber(courseFormModel.getPrereq());
        if(prereq.isPresent()){
            course.setPrereq(prereq.get());
        }else{
            course.setPrereq(null);
        }

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    /* -- */
    public CourseRepository getRepo(){
        return this.courseRepository;
    }


    public Course saveCourse(CourseUpdateFormModel courseUpdateFormModel) {
        System.out.println("in save course");
        Course course = courseRepository.findByCourseNumber(courseUpdateFormModel.getCourseNumber()).get();
        if(!courseUpdateFormModel.getTitle().isEmpty()){
            course.setTitle(courseUpdateFormModel.getTitle());
        }
        if(!courseUpdateFormModel.getDescription().isEmpty()){
            course.setDescription(courseUpdateFormModel.getDescription());
        }
        if(courseUpdateFormModel.getPrice() != null){
            course.setPrice(courseUpdateFormModel.getPrice());
        }
        System.out.println(course);
        return courseRepository.save(course);
    }

    public Section saveSection(SectionUpdateFormModel sectionUpdateFormModel) {
        System.out.println("in save section");
        Section section = sectionsRepository.findById(sectionUpdateFormModel.getId()).get();
        section.setSemester(sectionUpdateFormModel.getSemester());
        section.setYear(sectionUpdateFormModel.getYear());
        if (sectionUpdateFormModel.getSeatsAvailable() != section.getSeatsAvailable()) {
            section.setSeatsAvailable(sectionUpdateFormModel.getSeatsAvailable());
        }
        return sectionsRepository.save(section);
    }
    public Course getCourseByNumber(String courseNumber){
        Optional<Course> course =  courseRepository.findByCourseNumber(courseNumber);
        return course.orElse(null);
    }

    public Section getSectionById(Long id) {
        Optional<Section> section = sectionsRepository.findById(id);
        return section.orElse(null);
    }

    public List<Section> getSectionsByCourseNumber(String courseNumber){
        Optional<Course> course = courseRepository.findByCourseNumber(courseNumber);
        if(course.isPresent()) {
             if(sectionsRepository.findByCourse(course.get()).isPresent()){
                 return sectionsRepository.findByCourse(course.get()).get();
             }
        }
        return null;
    }


}
