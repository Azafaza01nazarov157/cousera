package org.example.cursera.controller.analisis;

import lombok.RequiredArgsConstructor;
import org.example.cursera.service.minio.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    @GetMapping("/presigned-download")
    public ResponseEntity<String> getPresignedDownloadUrl(@RequestParam String filePath) {
        String url = minioService.getPresignedDownloadUrl(filePath);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "uploads") String directory) {
        String fileUrl = minioService.uploadFile(file, directory).getFileUrl();
        return ResponseEntity.ok(fileUrl);
    }

}
