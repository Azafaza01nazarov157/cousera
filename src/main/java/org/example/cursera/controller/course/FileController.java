//package org.example.cursera.controller.course;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.example.cursera.domain.entity.MinioFile;
//import org.example.cursera.service.minio.MinioService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/files")
//@RequiredArgsConstructor
//@Tag(name = "File Management", description = "Operations related to file management")
//public class FileController {
//
//    private final MinioService fileService;
//
//    @Operation(summary = "Upload a file and associate it with a topic")
//    @PostMapping(value = "/upload", consumes = "multipart/form-data")
//    public ResponseEntity<MinioFile> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("userId") Long userId,
//            @RequestParam("topicId") Long topicId) throws Exception {
//        MinioFile uploadedFile = fileService.uploadFile(file, userId, topicId);
//        return ResponseEntity.ok(uploadedFile);
//    }
//
//    @Operation(summary = "Get all files associated with a specific topic")
//    @GetMapping("/topic/{topicId}")
//    public ResponseEntity<List<MinioFile>> getFilesByTopic(@PathVariable Long topicId) {
//        List<MinioFile> files = fileService.getFilesByTopic(topicId);
//        return ResponseEntity.ok(files);
//    }
//
//    @Operation(summary = "Get a file by its ID")
//    @GetMapping("/{id}")
//    public ResponseEntity<MinioFile> getFileById(@PathVariable Integer id) {
//        MinioFile file = fileService.getFile(id);
//        return ResponseEntity.ok(file);
//    }
//
//    @Operation(summary = "Get all files")
//    @GetMapping
//    public ResponseEntity<List<MinioFile>> getAllFiles() {
//        List<MinioFile> files = fileService.getAllFiles();
//        return ResponseEntity.ok(files);
//    }
//
//    @Operation(summary = "Delete a file by its ID")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFile(@PathVariable Integer id) throws Exception {
//        fileService.deleteFile(id);
//        return ResponseEntity.noContent().build();
//    }
//}
