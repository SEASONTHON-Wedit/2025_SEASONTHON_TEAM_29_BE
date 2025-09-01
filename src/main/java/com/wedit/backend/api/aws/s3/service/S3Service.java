package com.wedit.backend.api.aws.s3.service;

import com.wedit.backend.api.aws.s3.dto.ImagePutRequestDTO;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presign.expiration-minutes}")
    private Integer durationMinutes;


    public String generatePresignedPutUrl(
            String domain, String filename,
            String contentType, Long contentLength,
            Long memberId, Long entityId) {

        // S3 객체 키 생성
        String key = createS3Key(domain, memberId, entityId, contentType, filename);

        // PreSigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    public List<String> generatePresignedPutUrls(List<ImagePutRequestDTO> requestDTOs, Long memberId) {

        return requestDTOs.stream()
                .map(dto -> generatePresignedPutUrl(
                        dto.getDomain(),
                        dto.getFilename(),
                        dto.getContentType(),
                        dto.getContentLength(),
                        memberId,
                        dto.getEntityId()
                )).toList();
    }

    public String generatePresignedGetUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(durationMinutes))
                .build();

        // 추후 GET URL도 같이 반환해야 함.

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

//    public List<ImageGetResponseDTO> generatePresignedGetUrls(String domain, Long memberId, Long entityId) {
//
//      // 도메인 별 레포지토리에서 여러 이미지 조회 후 반환
//    }

    // DELETE Logic
    // 프론트가 준 파일 경로(키)를 기반으로 DB 조회 후 DB와 S3 모두에서 삭제

    // URL Generator
    private String createS3Key(String domain, Long memberId, Long entityId, String contentType, String originalFileName) {

        // 미디어 타입 분류
        String mediaType = contentType.toLowerCase().startsWith("image/") ? "images" : "videos";

        // 날짜 폴더 구조
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // UUID + 현재시각 + 원본파일명 (확장자 포함)
        String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + currentDateTime + "_" + safeFileName;

        // entityId 없다면 null
        String entityPart = (entityId == null) ? "null" : entityId.toString();

        // 최종 PreSigned URL 발급 후 반환
        // {domain}/{memberId}/{entityId}/{mediaType}/{uuid}_{currentDateTime}_{originalFileName}
        return String.format("%s/%d/%s/%s/%s", domain, memberId, entityPart, mediaType, fileName);
    }

    // 파일 누락, 중복 업로드, 미완료 파일 예외 유의
}