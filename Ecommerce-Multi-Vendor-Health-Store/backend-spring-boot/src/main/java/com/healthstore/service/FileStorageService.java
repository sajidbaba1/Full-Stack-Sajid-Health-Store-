package com.healthstore.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling file storage operations.
 * Provides methods to store and retrieve files from the server's file system.
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    /**
     * Initializes the file storage service by creating the uploads directory if it doesn't exist.
     * @throws RuntimeException if the upload directory cannot be created.
     */
    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Stores a file in the file system.
     * @param file The file to store.
     * @return The path where the file was stored.
     * @throws RuntimeException if the file cannot be stored.
     */
    public String storeFile(MultipartFile file) {
        // Generate a unique filename to prevent collisions
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        
        try {
            // Resolve the file path and copy the file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            
            // Return the relative URL path
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Loads a file as a Resource.
     * @param fileName The name of the file to load.
     * @return The Resource object representing the file.
     * @throws RuntimeException if the file cannot be found or read.
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }
}
