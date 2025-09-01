package com.wedit.backend.api.aws.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presigned.expiration-minutes}")
    private Integer durationMinutes;

    // PUT URL
    // 최종 URL 발급 전 DB(ex. reviewImage)에 임의의 S3 파일 경로 저장
    // 업로드 시 Content-Type 도 같이 저장
    // 업로드 상태 저장 필드 추가 (완료, 실패, 진행 등)
    // 조회용 URL도 발급
    // 발급 URL 로 PUT 성공/실패 시 핸들러 필요?
    // 혹은 타임아웃 스케줄링 구현
    public String generatePresignedPutUrl(
            String domain, String filename,
            String contentType, Long contentLength,
            Long memberId) {


    }

    // GET URL
    // reviewImage 의 파일 경로를 불러와 URL 생성 후 반환 (동적 생성)
    // reviewImage의 파일 경로도 함께 반환 -> 삭제 시 사용

    // DELETE Logic
    // 프론트가 준 파일 경로(키)를 기반으로 DB 조회 후 DB와 S3 모두에서 삭제

    // URL Generator
    // {domain}/{memberId}/{entityId}/{mediaType}/{uuid}-{originalFileName}
    private String createS3Key(String domain, Long memberId, Long entityId, String contentType, String originalFileName) {
        
        // 미디어 타입 분류
        String mediaType = contentType.toLowerCase().startsWith("image/") ? "images" : "videos";

        // 날짜 폴더 구조
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // UUID + 원본파일명 (확장자 포함)
        String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "-" + safeFileName;

        // entityId 없다면 null
        String entityPart = (entityId == null) ? "null" : entityId.toString();

        // 최종 PreSigned URL 발급 후 반환

    }
    
    // 파일 누락, 중복 업로드, 미완료 파일 예외 유의
}
