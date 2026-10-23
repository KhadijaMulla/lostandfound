
package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path uploadDir =
            Paths.get("uploads").toAbsolutePath().normalize();

    public ImageStorageService() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public String store(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 5 MB.");
        }

        String contentType = file.getContentType();

        Set<String> allowedTypes = Set.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        );

        if (contentType == null ||
                !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed.");
        }

        String extension = switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Unsupported image type.");
        };

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDir.resolve(filename).normalize();

        if (!destination.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        Files.copy(file.getInputStream(), destination);

        return filename;
    }

    public Path getImagePath(String filename) {
        Path path = uploadDir.resolve(filename).normalize();

        if (!path.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid image path.");
        }

        return path;
    }
}
