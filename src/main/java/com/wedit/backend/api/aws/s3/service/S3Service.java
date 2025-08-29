package com.wedit.backend.api.aws.s3.service;

import org.springframework.stereotype.Service;

@Service
public class S3Service {

    // GET URL
    // reviewImage 의 파일 경로를 불러와 URL 생성 후 반환 (동적 생성)
    // reviewImage의 파일 경로도 함께 반환 -> 삭제 시 사용

    // PUT URL
    // 최종 URL 발급 전 DB(ex. reviewImage)에 임의의 S3 파일 경로 저장
    // 업로드 시 Content-Type 도 같이 저장
    // 업로드 상태 저장 필드 추가 (완료, 실패, 진행 등)
    // 조회용 URL도 발급
    // 발급 URL 로 PUT 성공/실패 시 핸들러 필요?
    // 혹은 타임아웃 스케줄링 구현

    // DELETE Logic
    // 프론트가 준 파일 경로(키)를 기반으로 DB 조회 후 DB와 S3 모두에서 삭제

    // URL Generator
    
    // 파일 누락, 중복 업로드, 미완료 파일 예외 유의
}
