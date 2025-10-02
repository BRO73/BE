package com.example.restaurant_management.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;


@RestController
@RequestMapping("/api/files")
public class FileController {

    private final String storagePath = "src/main/resources/storage/";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            // Tạo tên file mới để tránh trùng
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // Tạo folder nếu chưa tồn tại
            File dir = new File(storagePath);
            if (!dir.exists()) dir.mkdirs();

            Path filepath = Paths.get(storagePath, filename);
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL để frontend dùng
            String url = "/storage/" + filename;
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not save file"));
        }
    }
}

