package com.AK.unilog.service;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import com.AK.unilog.repository.CourseRepository;
import com.AK.unilog.repository.SectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApiService {

    private final SectionsRepository sectionsRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public ApiService(SectionsRepository sectionsRepository, CourseRepository courseRepository) {
        this.sectionsRepository = sectionsRepository;
        this.courseRepository = courseRepository;
    }

    public List<Section> getSections() {
        return sectionsRepository.findAll();
    }

    public Object getCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseByNumber(String courseNumber) {
        return courseRepository.findByCourseNumber(courseNumber).get();
    }

    public List<Section> getSectionByCourseNumberSearch(String courseNumber, Sort sort){
        Optional<List<Section>> sectionList = sectionsRepository.findByCourseCourseNumberContainingIgnoreCase(courseNumber, sort);
        return sectionList.orElse(null);
    }

    public List<Course> getCourseByCourseNumberSearch(String courseNumber, Sort sort) {
        Optional<List<Course>> courseList = courseRepository.findByCourseNumberContainingIgnoreCase(courseNumber, sort);
        return courseList.orElse(null);
    }

    public List<Course> getAvailableCourseByNumberSearch(String courseNumber, Sort sort) {
        Optional<List<Course>> courseListOptional = courseRepository.findByDisabledFalseAndCourseNumberContainingIgnoreCase(courseNumber, sort);
        return courseListOptional.orElse(null);
    }

    //student section search
    public List<Section> getAvailableSectionByNumberSearch(String courseNumber, Sort sort) {
        Optional<List<Section>> sectionList = sectionsRepository.findByDisabledFalseAndCourseCourseNumberContainingIgnoreCase(courseNumber, sort);
        return sectionList.orElse(null);
    }

    public void toggleDisableCourse(String courseNumber) {
        Optional<Course> course = courseRepository.findByCourseNumber(courseNumber);
        if (course.isPresent()) {

            Course courseObj = course.get();

            Optional<List<Section>> optionalSectionList = sectionsRepository.findByCourse(courseObj);
            if(optionalSectionList.isPresent()){
                List<Section> sectionList = optionalSectionList.get();
                for(Section section : sectionList){
                    section.setDisabled(!section.isDisabled());
                    sectionsRepository.save(section);
                }
            }

            courseObj.setDisabled(!courseObj.isDisabled());
            courseRepository.save(courseObj);
        }
    }

    public void toggleDisableSection(Long id) {
        Optional<Section> section = sectionsRepository.findById(id);
        if (section.isPresent()) {
            Section sectionObj = section.get();
            sectionObj.setDisabled(!sectionObj.isDisabled());
            sectionsRepository.save(sectionObj);
        }
    }

    public List<Section> getAvailableSections() {
        Optional<List<Section>> listOfSections = sectionsRepository.findByDisabledFalse();
        return listOfSections.orElse(null);
    }

    public List<Course> getAvailableCourses() {
        Optional<List<Course>> listOfCourses = courseRepository.findByDisabledFalse();
        return listOfCourses.orElse(null);
    }

    public Section getSectionById(Long id) {
        Optional<Section> section = sectionsRepository.findById(id);
        return section.orElse(null);
    }

    public List<Section> findAllSectionsSort(Sort sort){
        return sectionsRepository.findAll(sort);
    }

    public List<Course> findAllCoursesSort(Sort sort){
        return courseRepository.findAll(sort);
    }
}
