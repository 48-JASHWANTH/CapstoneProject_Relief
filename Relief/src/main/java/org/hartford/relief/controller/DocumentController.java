package org.hartford.relief.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getDocument(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String filename = resource.getFilename();
                String ext = "";
                if (filename != null && filename.lastIndexOf('.') >= 0) {
                    ext = filename.substring(filename.lastIndexOf('.')).toLowerCase();
                }

                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                if (ext.equals(".png")) mediaType = MediaType.IMAGE_PNG;
                else if (ext.equals(".jpg") || ext.equals(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
                else if (ext.equals(".pdf")) mediaType = MediaType.APPLICATION_PDF;

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
