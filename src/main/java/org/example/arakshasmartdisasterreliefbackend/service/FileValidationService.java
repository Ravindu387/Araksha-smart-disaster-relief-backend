package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    // Allowed Image Extensions & corresponding MIME types
    private static final Map<String, String> ALLOWED_IMAGES = new HashMap<>();
    // Allowed Document Extensions & corresponding MIME types
    private static final Map<String, String> ALLOWED_DOCUMENTS = new HashMap<>();

    static {
        ALLOWED_IMAGES.put("jpg", "image/jpeg");
        ALLOWED_IMAGES.put("jpeg", "image/jpeg");
        ALLOWED_IMAGES.put("png", "image/png");

        ALLOWED_DOCUMENTS.put("pdf", "application/pdf");
        ALLOWED_DOCUMENTS.put("doc", "application/msword");
        ALLOWED_DOCUMENTS.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file.");
        }

        // 1. Validate File Size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds maximum allowed limit of 5 MB.");
        }

        // 2. Prevent Directory Traversal Attacks & validate filename
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new FileStorageException("Invalid filename.");
        }

        if (originalFileName.contains("..") || originalFileName.contains("/") || originalFileName.contains("\\")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFileName);
        }

        // 3. Extract File Extension
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (extension.isEmpty()) {
            throw new FileStorageException("File must have a valid extension.");
        }

        // 4. Validate MIME Type and Extension consistency
        String mimeType = file.getContentType();
        if (mimeType == null) {
            throw new FileStorageException("Failed to detect file type.");
        }

        boolean isAllowedImage = ALLOWED_IMAGES.containsKey(extension) && 
                                 ALLOWED_IMAGES.get(extension).equalsIgnoreCase(mimeType);
        boolean isAllowedDoc = ALLOWED_DOCUMENTS.containsKey(extension) && 
                               ALLOWED_DOCUMENTS.get(extension).equalsIgnoreCase(mimeType);

        // Additional MIME check for docx (some browsers upload it with generic octet-stream MIME type)
        if (extension.equals("docx") && "application/octet-stream".equalsIgnoreCase(mimeType)) {
            isAllowedDoc = true;
        }

        if (!isAllowedImage && !isAllowedDoc) {
            throw new FileStorageException("Unsupported file type or extension. Allowed formats: JPG, JPEG, PNG, PDF, DOC, DOCX.");
        }

        // 5. Explicit check for dangerous/executable extensions
        List<String> dangerousExtensions = Arrays.asList("exe", "bat", "sh", "js", "jsp", "class", "jar", "scr", "cmd", "vbs");
        if (dangerousExtensions.contains(extension)) {
            throw new FileStorageException("Rejected suspicious/executable file extension.");
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
