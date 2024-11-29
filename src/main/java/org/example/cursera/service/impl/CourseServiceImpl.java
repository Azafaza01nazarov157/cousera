package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.*;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.enums.RequestStatus;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.domain.repository.*;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.CourseService;
import org.example.cursera.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final MinioService minioService;
    private final MinioFileRepository minioFileRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GetCourseDto> findAll() {
        return courseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetCourseDto findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));
        return convertToDto(course);
    }

    @Override
    public List<GetCourseDto> getCourseByName(String name) {
        return courseRepository.findAllByName(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCourseDto> getCourseByModule(String moduleName) {
        return moduleRepository.findAllByName(moduleName).stream()
                .map(Module::getCourse)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createCourse(CreateCourseDto course) {
        if (course == null) {
            throw new NotFoundException(new ErrorDto("404", "Course not found"));
        }

        final User user = userRepository.findById(course.getModeratorId())
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (Role.MODERATOR != user.getRole()) {
            throw new ForbiddenException(new ErrorDto("403", "Forbidden! You don't have enough rights"));
        }

        Course courseEntity = Course.builder()
                .name(course.getName())
                .moderatorId(course.getModeratorId())
                .companyName(course.getCompanyName())
                .description(course.getDescription())
                .createAt(LocalDate.now())
                .modules(new ArrayList<>())
                .subscribers(new ArrayList<>())
                .build();

        Course savedCourse = courseRepository.save(courseEntity);

        if (course.getImage() != null && !course.getImage().isEmpty()) {
            MinioFileDto uploadedFile = minioService.uploadFile(course.getImage(), "courses/" + savedCourse.getId());

            MinioFile minioFile = MinioFile.builder()
                    .fileName(uploadedFile.getFileName())
                    .fileUrl(uploadedFile.getFileUrl())
                    .contentType(uploadedFile.getContentType())
                    .size(uploadedFile.getSize())
                    .uploadedAt(uploadedFile.getUploadedAt())
                    .course(savedCourse)
                    .build();

            MinioFile savedFile = minioFileRepository.save(minioFile);

            savedCourse.setImage(savedFile);
            courseRepository.save(savedCourse);
        }
    }

    private GetCourseDto convertToDto(Course course) {
        List<ModuleDto> modules = course.getModules().stream()
                .map(module -> {
                    ModelCourseDto modelCourseDto = ModelCourseDto.builder()
                            .id(module.getCourse().getId())
                            .name(module.getCourse().getName())
                            .description(module.getCourse().getDescription())
                            .companyName(module.getCourse().getCompanyName())
                            .createAt(module.getCourse().getCreateAt())
                            .moderatorId(module.getCourse().getModeratorId())
                            .build();

                    return ModuleDto.builder()
                            .id(module.getId())
                            .name(module.getName())
                            .course(List.of(modelCourseDto))
                            .lessons(module.getLessons().size())
                            .build();
                })
                .collect(Collectors.toList());
        String imageUrl = course.getImage() != null ? course.getImage().getFileUrl() : null;

        return GetCourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .companyName(course.getCompanyName())
                .createAt(course.getCreateAt())
                .moderatorId(course.getModeratorId())
                .modules(modules)
                .image(imageUrl)
                .build();
    }

    @Override
    @Transactional
    public void requestSubscription(Long courseId, Long userId) {
        final Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        SubscriptionRequest request = SubscriptionRequest.builder()
                .course(course)
                .user(user)
                .status(RequestStatus.PENDING)
                .build();

        subscriptionRequestRepository.save(request);
    }


    @Override
    public List<SubscriptionRequest> getUserSubscriptionRequests(Long courseId, Long userId) {
        if(courseId == null || userId == null) {
            throw new ForbiddenException(new ErrorDto("404", "Course or User not found"));
        }
        final Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Пользователь не найден")));

        return subscriptionRequestRepository.findByCourseIdAndUserId(courseId, userId);
    }



    @Override
    public List<GetModuleDto> getModuleByCourseId(Long courseId) {
        List<Module> modules = moduleRepository.findByCourseId(courseId);

        if (modules.isEmpty()) {
            throw new NotFoundException(new ErrorDto("404", "Пользователь не найден"));
        }

        return modules.stream().map(this::convertToModuleDto).toList();
    }

    private GetModuleDto convertToModuleDto(Module module) {
        return GetModuleDto.builder()
                .id(module.getId())
                .name(module.getName())
                .lessons(module.getLessons().size())
                .build();
    }


    public List<CourseDto> getSubscribedCourses(final User user) {
        return subscriptionRequestRepository.findByUserAndStatus(user, RequestStatus.APPROVED)
                .stream()
                .map(SubscriptionRequest::getCourse)
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Course entity to a CourseDto.
     *
     * @param course The course entity to be mapped.
     * @return The mapped CourseDto.
     */
    private CourseDto mapToCourseDto(final Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .companyName(course.getCompanyName())
                .createAt(course.getCreateAt())
                .moderatorId(course.getModeratorId())
                .image(course.getImage().getFileUrl())
                .build();
    }


}
