package com.healthstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for storing and retrieving files on the server.
 */
@Service
public class FileStorageService {

    private final Path rootLocation;

    /**
     * Initializes the file storage service with the specified upload directory.
     * 
     * @param uploadDir The base directory where files will be stored.
     */
    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage location", e);
        }
    }

    /**
     * Stores a file in the file system.
     * 
     * @param file The file to store.
     * @return The generated filename.
     * @throws FileStorageException If the file cannot be stored.
     */
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Invalid file path sequence: " + originalFileName);
        }

        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + originalFileName);
            }

            // Generate a unique filename to prevent collisions
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to the target location
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(uniqueFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + originalFileName, e);
        }
    }

    /**
     * Loads a file as a Resource.
     * 
     * @param filename The name of the file to load.
     * @return The file as a Resource.
     * @throws FileNotFoundException If the file cannot be found.
     */
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    /**
     * Deletes a file from the file system.
     * 
     * @param filename The name of the file to delete.
     * @return true if the file was deleted, false if it didn't exist.
     * @throws FileStorageException If the file cannot be deleted.
     */
    public boolean deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file " + filename, e);
        }
    }

    /**
     * Gets the root location where files are stored.
     * 
     * @return The root location path.
     */
    public Path getRootLocation() {
        return rootLocation;
    }
}
