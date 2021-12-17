package com.AK.unilog.service;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.Section;
import com.AK.unilog.repository.CourseRepository;
import com.AK.unilog.repository.SectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Course> getCourseByNumberRegex(String regex) {
        List<Course> courseList = courseRepository.findAll();
        ArrayList<Course> matchingCourses = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getCourseNumber().matches(regex)) {
                matchingCourses.add(course);
            }
        }
        return matchingCourses;
    }

    public List<Course> getAvailableCourseByNumberRegex(String regex) {
        Optional<List<Course>> courseListOptional = courseRepository.findByDisabledFalse();
        ArrayList<Course> matchingCourses = new ArrayList<>();
        if (courseListOptional.isPresent()) {
            List<Course> courseList = courseListOptional.get();
            for (Course course : courseList) {
                if (course.getCourseNumber().matches(regex)) {
                    matchingCourses.add(course);
                }
            }
        }
        return matchingCourses;
    }

    //FIXME this needs to be like in the repo
    public List<Section> getSectionByNumberRegex(String regex) {
        List<Section> sectionList = sectionsRepository.findByDisabledFalse().get();
        ArrayList<Section> matchingSections = new ArrayList<>();
        for (Section section : sectionList) {
            if (section.getCourse().getCourseNumber().matches(regex)) {
                matchingSections.add(section);
            }
        }
        return matchingSections;
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

    public List<Section> getAvailableSectionsByCourse(String courseNumber) {
        Optional<Course> course = courseRepository.findByCourseNumber(courseNumber);
        if (course.isPresent()) {
            Optional<List<Section>> listOfSections = sectionsRepository.findByCourseAndDisabledIsFalse(course.get());
            if (listOfSections.isPresent()) {
                System.out.println("list of sections by course number" + listOfSections.get());
                return listOfSections.get();
            }
        }
        System.out.println("no available section with " + courseNumber);
        return null;
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
}
