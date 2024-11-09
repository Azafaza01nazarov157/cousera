package org.example.cursera.service.course;


import org.example.cursera.domain.dtos.GetUsersModuleDto;

public interface ModuleService {

    /**
     * Создать новый модуль в указанном курсе.
     *
     * @param courseId   Идентификатор курса.
     * @param moduleName Название нового модуля.
     */
    void createModule(Long courseId, String moduleName);

    /**
     * Получить детали модуля по его идентификатору.
     *
     * @param moduleId Идентификатор модуля.
     * @return Объект ModuleDto с деталями модуля.
     */
    GetUsersModuleDto findModuleById(Long moduleId);
}
