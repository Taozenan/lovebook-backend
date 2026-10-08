package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopCosStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shop")
public class ShopUploadController {
    private final CurrentUserService currentUserService;
    private final ShopCosStorageService cosStorageService;
    private final Path uploadRoot;

    public ShopUploadController(
            CurrentUserService currentUserService,
            ShopCosStorageService cosStorageService,
            @Value("${shop.upload-dir:uploads}") String uploadDir) {
        this.currentUserService = currentUserService;
        this.cosStorageService = cosStorageService;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> upload(
            @RequestHeader("Authorization") String authorization,
            @RequestPart("file") MultipartFile file) {
        long userId = currentUserService.requireUserId(authorization);
        if (cosStorageService.isEnabled()) {
            String url = cosStorageService.upload(userId, file);
            return ApiResponse.success(
                    Map.of("url", url, "filename", url.substring(url.indexOf("key=") + 4)));
        }
        return uploadLocal(userId, file);
    }

    @GetMapping("/upload/object")
    public ResponseEntity<Resource> downloadCosObject(@RequestParam("key") String key) {
        Resource resource = cosStorageService.getObject(key);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = cosStorageService.contentType(key);
        MediaType mediaType =
                contentType == null
                        ? MediaType.APPLICATION_OCTET_STREAM
                        : MediaType.parseMediaType(contentType);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @GetMapping("/upload/{filename:.+}")
    public ResponseEntity<Resource> downloadLocal(@PathVariable("filename") String filename)
            throws IOException {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }
        Path file = uploadRoot.resolve(filename).normalize();
        if (!file.startsWith(uploadRoot) || !Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);
        MediaType mediaType =
                contentType == null
                        ? MediaType.APPLICATION_OCTET_STREAM
                        : MediaType.parseMediaType(contentType);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    private ApiResponse<Map<String, String>> uploadLocal(long userId, MultipartFile file) {
        validate(file);
        String extension = extension(file.getOriginalFilename(), file.getContentType());
        String filename = userId + "_" + UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            Files.createDirectories(uploadRoot);
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, uploadRoot.resolve(filename));
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("图片保存失败");
        }
        return ApiResponse.success(Map.of("url", "/api/shop/upload/" + filename, "filename", filename));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择图片");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("图片不能超过5MB");
        }
        String contentType =
                file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
    }

    private String extension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            String extension =
                    originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)")) {
                return extension;
            }
        }
        return switch (contentType == null ? "" : contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/bmp" -> ".bmp";
            default -> ".jpg";
        };
    }
}
