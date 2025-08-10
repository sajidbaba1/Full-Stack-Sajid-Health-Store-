package com.healthstore.controller;

import com.healthstore.exception.FileNotFoundException;
import com.healthstore.exception.FileStorageException;
import com.healthstore.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

/**
 * REST controller for handling file upload and download operations.
 */
@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Upload a file.
     * 
     * @param file The file to upload.
     * @return A response with the file download URI.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();
            
            return ResponseEntity.ok(new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize()));
        } catch (FileStorageException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Download a file.
     * 
     * @param fileName The name of the file to download.
     * @return The file as a resource.
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Load file as Resource
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            // Try to determine file's content type
            String contentType = "application/octet-stream";
            try {
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (IOException ex) {
                // Default content type if couldn't determine
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a file.
     * 
     * @param fileName The name of the file to delete.
     * @return A response indicating success or failure.
     */
    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            boolean deleted = fileStorageService.deleteFile(fileName);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (FileStorageException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Response object for file uploads.
     */
    private static class FileUploadResponse {
        private final String fileName;
        private final String fileDownloadUri;
        private final String fileType;
        private final long size;
        
        public FileUploadResponse(String fileName, String fileDownloadUri, String fileType, long size) {
            this.fileName = fileName;
            this.fileDownloadUri = fileDownloadUri;
            this.fileType = fileType;
            this.size = size;
        }
        
        // Getters
        public String getFileName() {
            return fileName;
        }
        
        public String getFileDownloadUri() {
            return fileDownloadUri;
        }
        
        public String getFileType() {
            return fileType;
        }
        
        public long getSize() {
            return size;
        }
    }
}
