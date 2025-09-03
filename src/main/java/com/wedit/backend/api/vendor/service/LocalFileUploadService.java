package com.wedit.backend.api.vendor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileUploadService {

    @Value("${file.upload.base-dir:uploads/}")
    private String baseUploadDir;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.server.url:}")
    private String serverUrl;

    // 허용되는 파일 확장자
    private final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    
    // 최대 파일 크기 (10MB)
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 단일 파일을 로컬 디렉토리에 저장
     */
    public String uploadFile(MultipartFile file, String domain, Long entityId) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 파일 유효성 검사
        validateFile(file);

        try {
            // 디렉토리 경로 생성: uploads/{domain}/{entityId}/images/
            String directoryPath = createDirectoryPath(domain, entityId);
            
            // 디렉토리 생성
            Path dirPath = Paths.get(directoryPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("업로드 디렉토리 생성: {}", directoryPath);
            }

            // 안전한 파일명 생성
            String savedFileName = generateSafeFileName(file.getOriginalFilename());
            
            // 파일 저장
            Path targetLocation = Paths.get(directoryPath + savedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // URL 생성
            String fileUrl = generateFileUrl(domain, entityId, savedFileName);
            
            log.info("파일 저장 완료: originalName={}, savedName={}, size={}, url={}", 
                file.getOriginalFilename(), savedFileName, file.getSize(), fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("파일 저장 실패: fileName={}, error={}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 여러 파일을 로컬 디렉토리에 저장
     */
    public List<String> uploadFiles(List<MultipartFile> files, String domain, Long entityId) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> uploadedUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String uploadedUrl = uploadFile(file, domain, entityId);
            if (uploadedUrl != null) {
                uploadedUrls.add(uploadedUrl);
            }
        }
        
        return uploadedUrls;
    }

    /**
     * 디렉토리 경로 생성
     */
    private String createDirectoryPath(String domain, Long entityId) {
        String entityPart = (entityId == null) ? "temp" : entityId.toString();
        return String.format("%s%s/%s/images/", baseUploadDir, domain, entityPart);
    }

    /**
     * 안전한 파일명 생성
     */
    private String generateSafeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new RuntimeException("파일명이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFileName);
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String uuid = UUID.randomUUID().toString().substring(0, 8); // 짧은 UUID 사용
        
        return String.format("%s_%s_%s%s", uuid, currentDateTime, safeFileName, extension);
    }

    /**
     * 파일 URL 생성 (환경에 따라 동적으로 생성)
     */
    private String generateFileUrl(String domain, Long entityId, String fileName) {
        String entityPart = (entityId == null) ? "temp" : entityId.toString();
        
        // 배포 환경에서는 serverUrl 사용, 로컬에서는 localhost 사용
        String baseUrl;
        if (serverUrl != null && !serverUrl.isEmpty()) {
            baseUrl = serverUrl;
        } else {
            baseUrl = String.format("http://localhost:%s", serverPort);
        }
        
        return String.format("%s/uploads/%s/%s/images/%s", 
            baseUrl, domain, entityPart, fileName);
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }
        
        // 파일 크기 검사
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(String.format("파일 크기가 너무 큽니다. 최대 크기: %dMB", 
                MAX_FILE_SIZE / (1024 * 1024)));
        }
        
        // 파일 확장자 검사
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new RuntimeException("파일명이 올바르지 않습니다.");
        }
        
        String extension = getFileExtension(originalFileName).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new RuntimeException("지원하지 않는 파일 형식입니다. 지원 형식: " + 
                String.join(", ", ALLOWED_EXTENSIONS));
        }
        
        // Content Type 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new RuntimeException("파일 확장자가 없습니다.");
        }
        
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            log.warn("삭제할 파일 URL이 비어있음");
            return;
        }
        
        try {
            // URL에서 파일 경로 추출
            String filePath = extractFilePathFromUrl(fileUrl);
            Path targetPath = Paths.get(filePath);
            
            boolean deleted = Files.deleteIfExists(targetPath);
            
            if (deleted) {
                log.info("파일 삭제 완료: {}", fileUrl);
            } else {
                log.warn("삭제할 파일이 존재하지 않음: {}", fileUrl);
            }
            
        } catch (IOException e) {
            log.error("파일 삭제 실패: fileUrl={}, error={}", fileUrl, e.getMessage(), e);
        }
    }

    /**
     * URL에서 파일 경로 추출
     */
    private String extractFilePathFromUrl(String fileUrl) {
        // http://localhost:8080/uploads/vendor/wedding_hall/1/images/filename.jpg 
        // -> uploads/vendor/wedding_hall/1/images/filename.jpg
        int uploadsIndex = fileUrl.indexOf("/uploads/");
        if (uploadsIndex != -1) {
            return fileUrl.substring(uploadsIndex + 1); // '/' 제거
        }
        throw new RuntimeException("올바르지 않은 파일 URL입니다: " + fileUrl);
    }
}
