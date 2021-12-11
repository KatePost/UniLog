package com.AK.unilog.repository;

import com.AK.unilog.entity.Course;
import com.AK.unilog.entity.Section;
import com.AK.unilog.service.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionsRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByCourseAndYearAndSemester(Course course, int year, Semester semester );
}
