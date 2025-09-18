package com.wedit.backend.api.aws.s3.service;

import com.wedit.backend.api.aws.s3.dto.PresignedUrlRequestDTO;
import com.wedit.backend.api.aws.s3.dto.PresignedUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presign.expiration-minutes}")
    private Integer durationMinutes;

    @Value("${cloud.aws.cloudfront.url}")
    private String cdnBaseUrl;


    public PresignedUrlResponseDTO  generatePresignedPutUrl(
            PresignedUrlRequestDTO reqDto,
            String domain,
            Long memberId) {

        // S3 객체 키 생성
        String key = createS3Key(domain, memberId, reqDto.getDomainId(), reqDto.getContentType(), reqDto.getFilename());

        // PreSigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(reqDto.getContentType())
                .contentLength(reqDto.getContentLength())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return PresignedUrlResponseDTO.builder()
                .s3Key(key)
                .presignedUrl(url)
                .contentType(reqDto.getContentType())
                .contentLength(reqDto.getContentLength())
                .build();
    }

    // 단일 조회 CDN URL 변환
    public String toCdnUrl(String key) {

        if (key == null || key.isBlank()) {
            return null;
        }

        return cdnBaseUrl + "/" + key;
    }

    // 다중 조회 CDN URL 리스트 반환
    public List<String> toCdnUrls(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return keys.stream()
                .map(this::toCdnUrl)
                .collect(Collectors.toList());
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("S3 file deleted : {}", key);
    }

    public void deleteFiles(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> toDelete = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(toDelete).build())
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);
        log.info("S3 file deleted : {} items", keys.size());
    }

    // URL Generator
    private String createS3Key(String domain, Long memberId, Long domainId, String contentType, String originalFileName) {

        // 미디어 타입 분류
        String mediaType;
        String lowerContentType = contentType.toLowerCase();
        if (lowerContentType.startsWith("image/")) {
            mediaType = "images";
        } else if (lowerContentType.startsWith("video/")) {
            mediaType = "videos";
        } else if (lowerContentType.startsWith("audio/")) {
            mediaType = "audios";
        } else {
            mediaType = "others";
        }

        // 날짜 폴더 구조
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // UUID 생성
        String uuid = UUID.randomUUID().toString();

        // 도메인 ID, 없으면 0
        Long finalDomainId = (domainId != null) ? domainId : 0L;

        // 확장자 추출
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String safeFileName = uuid + "_" + currentDateTime + extension;

        // 최종 PreSigned URL 발급 후 반환
        // {domain}/{memberId}/{mediaType}/{finalDomainId}/{uuid}_{currentDateTime}.{extension}
        return String.format("%s/%d/%s/%d/%s", domain, memberId, mediaType, finalDomainId, safeFileName);
    }
}