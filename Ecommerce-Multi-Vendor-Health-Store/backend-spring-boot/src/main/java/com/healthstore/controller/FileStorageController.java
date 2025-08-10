package com.healthstore.controller;

import com.healthstore.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Controller for handling file upload and download operations.
 * Provides endpoints for uploading files and downloading them.
 */
@RestController
@RequestMapping("/api")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    /**
     * Constructs a new FileStorageController with the required service.
     * @param fileStorageService The service for file storage operations.
     */
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Handles file upload requests.
     * @param file The file to be uploaded.
     * @return A response containing the download URL of the uploaded file.
     */
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Store the file and get its path
            String fileName = fileStorageService.storeFile(file);
            
            // Create the download URL for the file
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/downloadFile/")
                    .path(fileName)
                    .toUriString();
                    
            return new ResponseEntity<>(fileDownloadUri, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handles file download requests.
     * @param fileName The name of the file to download.
     * @return The file as a downloadable resource.
     */
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Load the file as a resource
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            // Determine the file's content type
            String contentType = "application/octet-stream";
            
            // Return the file with appropriate headers for download
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
