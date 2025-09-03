package com.wedit.backend.api.aws.s3.service;

import com.wedit.backend.api.aws.s3.dto.PresignedUrlRequestDTO;
import com.wedit.backend.api.aws.s3.dto.PresignedUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presigned.expiration-minutes}")
    private Integer durationMinutes;


    public PresignedUrlResponseDTO generatePresignedPutUrl(
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

        return new PresignedUrlResponseDTO(url);
    }

    public PresignedUrlResponseDTO generatePresignedGetUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .build();

        String url = s3Presigner.presignGetObject(presignRequest).url().toString();

        return new PresignedUrlResponseDTO(url);
    }

    // URL Generator
    private String createS3Key(String domain, Long memberId, Long domainId, String contentType, String originalFileName) {

        // 미디어 타입 분류
        String mediaType = contentType.toLowerCase().startsWith("image/") ? "images" : "videos";

        // 날짜 폴더 구조
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // UUID + 현재시각 + 원본파일명 (확장자 포함)
        String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + currentDateTime + "_" + safeFileName;

        // 최종 PreSigned URL 발급 후 반환
        // {domain}/{memberId}/{mediaType}/{domainId}/{uuid}_{currentDateTime}_{originalFileName}
        return String.format("%s/%d/%s/%s", domain, memberId, mediaType, domainId, fileName);
    }
}