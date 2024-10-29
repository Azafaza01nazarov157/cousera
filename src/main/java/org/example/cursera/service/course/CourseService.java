package org.example.cursera.service.course;

import org.example.cursera.domain.dtos.CreateCourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;

import java.util.List;

public interface CourseService {

    List<GetCourseDto> findAll();

    GetCourseDto findById(Long id);

    List<GetCourseDto> getCourseByName(String name);

    List<GetCourseDto> getCourseByModule(String moduleName);

    void createCourse(CreateCourseDto course);
}
