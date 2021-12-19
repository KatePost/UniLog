package com.AK.unilog.repository;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.Section;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseNumber(String courseNumber);

    Optional<List<Course>> findByDisabledFalse();

    //student course search
    //disabled is false and ignore case courseNumber search
    Optional<List<Course>> findByDisabledFalseAndCourseNumberContainingIgnoreCase(String courseNumber, Sort sort);

    Optional<List<Course>> findByCourseNumberContainingIgnoreCase(String courseNumber, Sort sort);
}
