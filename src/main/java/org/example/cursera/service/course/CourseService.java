package org.example.cursera.service.course;

import org.example.cursera.domain.dtos.CourseDto;
import org.example.cursera.domain.dtos.CreateCourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.GetModuleDto;
import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.RequestStatus;

import java.util.List;

public interface CourseService {

    List<GetCourseDto> findAll();

    GetCourseDto findById(Long id);

    List<GetCourseDto> getCourseByName(String name);

    List<GetCourseDto> getCourseByModule(String moduleName);

    void createCourse(CreateCourseDto course);

    void requestSubscription(Long courseId, Long userId);

    List<SubscriptionRequest> getUserSubscriptionRequests(Long courseId, Long userId);

    List<GetModuleDto> getModuleByCourseId(Long courseId);

    List<CourseDto> getSubscribedCourses(User user);
}
