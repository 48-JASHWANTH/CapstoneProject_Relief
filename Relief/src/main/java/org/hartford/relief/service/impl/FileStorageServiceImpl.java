package org.hartford.relief.service.impl;

import org.hartford.relief.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    @Override
    public String storeFile(MultipartFile file, Long policyId) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot store empty file.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds the 5MB limit.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("application/pdf"))) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only JPG, PNG, and PDF files are allowed.");
        }

        String rawName = file.getOriginalFilename();
        String originalFileName = StringUtils.cleanPath(rawName != null ? rawName : "doc.png");
        
        if (originalFileName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename contains invalid path sequence.");
        }

        String fileExtension = "";
        
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        
        String newFileName = "policy_" + policyId + "_" + UUID.randomUUID() + fileExtension;

        Path targetLocation = this.fileStorageLocation.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }
}
