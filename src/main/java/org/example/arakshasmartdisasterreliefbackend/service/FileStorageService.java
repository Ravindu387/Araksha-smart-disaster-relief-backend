package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.UploadedFile;
import org.example.arakshasmartdisasterreliefbackend.exception.FileStorageException;
import org.example.arakshasmartdisasterreliefbackend.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final UploadedFileRepository repository;

    @Autowired
    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDir, UploadedFileRepository repository) {
        this.repository = repository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public UploadedFile storeFile(MultipartFile file, String uploadedBy) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the filename contains invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence: " + originalFileName);
            }

            // Generate a unique stored name: UUID + file extension
            String extension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFileName.substring(dotIndex);
            }
            String storedFileName = UUID.randomUUID().toString() + extension;

            // Copy file to the target location (preventing name collision as stored name is unique UUID)
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save metadata
            UploadedFile uploadedFile = UploadedFile.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedBy(uploadedBy == null || uploadedBy.isBlank() ? "SYSTEM" : uploadedBy)
                    .uploadedDate(LocalDateTime.now())
                    .filePath(targetLocation.toString())
                    .build();

            return repository.save(uploadedFile);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found or path is invalid: " + fileName, ex);
        }
    }

    public UploadedFile getMetadata(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FileStorageException("File metadata not found for ID: " + id));
    }

    public void deleteFile(Long id) {
        UploadedFile fileMetadata = getMetadata(id);
        Path targetPath = Paths.get(fileMetadata.getFilePath());

        try {
            // Delete physical file
            Files.deleteIfExists(targetPath);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to delete physical file from storage: " + fileMetadata.getStoredFileName(), ex);
        }

        // Delete metadata
        repository.delete(fileMetadata);
    }
}
