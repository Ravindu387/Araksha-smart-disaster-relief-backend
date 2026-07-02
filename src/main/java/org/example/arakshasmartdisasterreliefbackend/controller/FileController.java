package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.UploadFileResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.UploadedFile;
import org.example.arakshasmartdisasterreliefbackend.service.FileStorageService;
import org.example.arakshasmartdisasterreliefbackend.service.FileValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileValidationService fileValidationService;

    @PostMapping("/api/files/upload")
    public UploadFileResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy) {

        // Validate file size, extension, MIME type, security traversal
        fileValidationService.validateFile(file);

        // Store file
        UploadedFile uploadedFile = fileStorageService.storeFile(file, uploadedBy);

        // Build file URL for retrieval via GET /uploads/{filename}
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(uploadedFile.getStoredFileName())
                .toUriString();

        return UploadFileResponse.builder()
                .id(uploadedFile.getId())
                .originalFileName(uploadedFile.getOriginalFileName())
                .storedFileName(uploadedFile.getStoredFileName())
                .fileType(uploadedFile.getFileType())
                .fileSize(uploadedFile.getFileSize())
                .fileUrl(fileUrl)
                .build();
    }

    @GetMapping("/api/files/{id}")
    public ResponseEntity<UploadedFile> getFileMetadata(@PathVariable Long id) {
        UploadedFile metadata = fileStorageService.getMetadata(id);
        return ResponseEntity.ok(metadata);
    }

    @DeleteMapping("/api/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileStorageService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Keep default fallback
        }

        // Fallback to default octet stream contentType
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
