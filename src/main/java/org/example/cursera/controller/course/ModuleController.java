package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.GetUsersModuleDto;
import org.example.cursera.domain.dtos.ModuleDto;
import org.example.cursera.domain.dtos.ModuleUserDto;
import org.example.cursera.service.course.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/modules")
@CrossOrigin(origins = "${application.cors.allowed-origins-base}")
public class ModuleController {

    private final ModuleService moduleService;


    @Operation(summary = "Create a new module", description = "Create a new module in the specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Module created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{courseId}/create")
    public ResponseEntity<Void> createModule(
            @PathVariable Long courseId,
            @RequestParam String moduleName) {
        try {
            moduleService.createModule(courseId, moduleName);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Get module details", description = "Retrieve details of a module by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/{moduleId}")
    public ResponseEntity<GetUsersModuleDto> findModuleById(@PathVariable Long moduleId) {
        try {
            GetUsersModuleDto moduleDto = moduleService.findModuleById(moduleId);
            if (moduleDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(moduleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Operation(summary = "Get user-specific module details", description = "Retrieve details of a module by its ID for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User module details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @GetMapping("/{moduleId}/user")
    public ResponseEntity<ModuleUserDto> findUserModuleById(@PathVariable Long moduleId) {
        try {
            ModuleUserDto moduleDto = moduleService.findUserModuleById(moduleId);
            if (moduleDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(moduleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

