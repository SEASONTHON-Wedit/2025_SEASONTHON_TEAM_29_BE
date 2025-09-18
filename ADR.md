# Architecture Decision Record (ADR) - Wedit

> **웨딩 플래닝 서비스, Wedit 아키텍처 의사결정 기록**  
> 작성자: 오현우, 김현빈

---

## 목차

1. [전체 시스템 아키텍처](#1-전체-시스템-아키텍처)
2. [백엔드 기술 스택](#2-백엔드-기술-스택)
3. [데이터베이스 설계](#3-데이터베이스-설계)
4. [보안 아키텍처](#4-보안-아키텍처)
5. [API 설계](#5-api-설계)
6. [파일 처리](#6-파일-처리)
7. [인프라 및 배포](#7-인프라-및-배포)
8. [모니터링 및 로깅](#8-모니터링-및-로깅)
9. [성능 최적화](#9-성능-최적화)
10. [확장성 고려사항](#10-확장성-고려사항)

---

## 1. 전체 시스템 아키텍처

### ADR-001: 마이크로서비스 vs 모놀리식 아키텍처

**결정**: 모놀리식 아키텍처 채택

**상황**
- 시즌톤 해커톤 프로젝트로 짧은 개발 기간
- 팀 규모가 작음 (백엔드 개발자 2명)
- 웨딩 도메인의 비즈니스 요구사항이 명확함
- 빠른 프로토타이핑 요구

**선택지**
1. 마이크로서비스 아키텍처
2. 모놀리식 아키텍처

**결정 이유**
- ✅ **개발 속도**: 빠른 프로토타이핑 가능
- ✅ **운영 복잡성 최소화**: 단일 배포 단위
- ✅ **팀 규모에 적합**: 적은 인원으로 관리 가능
- ✅ **비용 효율성**: 최소 비용으로 인프라 구성 가능

**Trade-Offs**
- **단일 장애점(SPOF)**: 하나의 컴포넌트 장애가 전체 서비스 중단 가능성 존재
- **배포 경직성**: 작은 수정사항도 전체 애플리케이션의 재배포 요구

**결과**
- Spring Boot 단일 애플리케이션으로 구성
- 도메인별 패키지 구조로 분리하여 결합도를 낮추는데 집중
- 도메인별 패키지 분리로 향후 마이크로서비스 전환 준비

---

## 2. 백엔드 기술 스택

### ADR-002: Java 21 + Spring Boot 3.5.5 선택

**결정**: Java 21 + Spring Boot 3.5.5 사용

**상황**
- 최신 기술 활용으로 성능과 생산성 향상 필요
- 장기적인 유지보수성 고려

**선택지**
1. Java 8 + Spring Boot 2.x
2. Java 17 + Spring Boot 3.x
3. Java 21 + Spring Boot 3.x

**결정 이유**
- ✅ **최신 LTS 버전**: Java 21의 안정성과 성능 개선
- ✅ **Virtual Threads**: 높은 동시성 처리 가능
- ✅ **Record Classes**: 간결한 DTO 작성
- ✅ **Spring Boot 3.x**: Native 이미지 지원, 성능 개선

---

### ADR-003: QueryDSL vs JPA Criteria API

**결정**: QueryDSL 채택

**상황**
- 복잡한 동적 쿼리 작성 필요
- 지역별, 가격별, 스타일별 다중 조건 검색

**선택지**
1. JPA Criteria API
2. QueryDSL
3. Native Query

**결정 이유**
- ✅ **타입 안정성**: 컴파일 타임 쿼리 오류 검출
- ✅ **가독성**: SQL과 유사한 직관적 문법
- ✅ **동적 쿼리**: BooleanBuilder로 조건부 쿼리 작성 용이

---

## 3. 데이터베이스 설계

### ADR-004: MySQL vs PostgreSQL

**결정**: MySQL 8.0 채택

**상황**
- 웨딩 업체 및 예약 데이터 저장
- 지역별 계층 구조 데이터 처리
- AWS RDS 사용 예정

**선택지**
1. MySQL
2. PostgreSQL
3. MongoDB (NoSQL)

**결정 이유**
- ✅ **AWS RDS 최적화**: Aurora MySQL 호환성
- ✅ **팀 친숙도**: 기존 MySQL 경험 보유
- ✅ **JSON 지원**: MySQL 8.0의 향상된 JSON 기능
- ✅ **성능**: 읽기 중심 워크로드에 적합

### ADR-005: 계층형 지역 데이터 구조

**결정**: Self-Referencing 테이블 구조 + 계층별 쿼리 최적화

**상황**
- 시/도(level 1) → 시/군/구(level 2) → 읍/면/동(level 3) 계층 구조
- level 2 입력 시 모든 하위 level 3 지역 검색 필요

**선택지**
1. Adjacency List (Self-Referencing)
2. Nested Set Model
3. Materialized Path

**결정 이유**
- ✅ **단순성**: 이해하기 쉬운 구조
- ✅ **확장성**: 새로운 지역 추가 용이
- ✅ **쿼리 최적화**: 별도 메서드로 계층별 조회

**구현**
```java
@Entity
public class Region {
    @Id
    private Long id;
    
    private String name;
    private int level;  // 1: 시/도, 2: 시/군/구, 3: 읍/면/동
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;
}

// 최적화된 조회 쿼리
@Query("SELECT r.code FROM Region r WHERE r.parent.code = :parentCode AND r.level = 3")
List<String> findLevel3CodesByParentCode(@Param("parentCode") String parentCode);
```

---

## 4. 보안 아키텍처

### ADR-006: JWT + OAuth2 하이브리드 인증

**결정**: JWT Access Token + OAuth2 소셜 로그인 조합

**상황**
- 간편한 회원가입 및 로그인 필요
- 보안성과 사용성 두 가지 모두 중요

**선택지**
1. 세션 기반 인증
2. JWT 단독
3. OAuth2 + JWT 조합

**결정 이유**
- ✅ **Stateless**: 서버 확장성 향상, 향후 수평 확장 시 세션 불일치 문제 제거
- ✅ **소셜 로그인**: 사용자 진입 장벽 낮춤
- ✅ **토큰 관리**: Access/Refresh Token 분리 및 Refresh Token Rotate 전략 수립
- ✅ **다중 플랫폼**: 웹/모바일 대응 가능
- ✅ **사용자 편의성**: 카카오, 네이버, 구글 소셜 로그인 지원

---

### ADR-007: SMS 인증 시스템

**결정**: CoolSMS API 활용

**상황**
- 휴대폰 번호 기반 본인 인증 필요

**선택지**
1. 자체 SMS 시스템 구축
2. CoolSMS API
3. AWS SNS

**결정 이유**
- ✅ **국내 특화**: 한국 통신사 최적화
- ✅ **안정성**: 높은 전송 성공률
- ✅ **개발 효율성**: 빠른 구현 가능
- ✅ **비용 효율성**: 소규모 서비스에 적합

---

## 5. API 설계

### ADR-008: RESTful API + OpenAPI 3.0

**결정**: REST 아키텍처 + Swagger UI 문서화

**상황**
- 프론트엔드와의 명확한 API 계약 필요
- API 문서 자동화 요구

**선택지**
1. REST API
2. GraphQL
3. gRPC

**결정 이유**
- ✅ **표준성**: HTTP 표준 활용
- ✅ **캐싱**: HTTP 캐싱 메커니즘 활용
- ✅ **도구 생태계**: Swagger/OpenAPI 지원
- ✅ **학습 곡선**: 팀 친숙도 높음

---

### ADR-009: 통합 응답 형식

**결정**: 표준화된 ApiResponse 래퍼 사용

**상황**
- 일관된 API 응답 형식 필요
- 성공/실패 상태 명확한 구분

**구현**
```java
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final String code;
    
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, SuccessStatus status) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .message(status.getMessage())
            .code(status.getCode())
            .build());
    }
}
```

---

## 6. 파일 처리

### ADR-010: AWS S3 + CloudFront

**결정**: S3 저장 + CloudFront CDN 제공 조합

**상황**
- 사용자 및 업체의 이미지, 비디오, 오디오 저장
- 전국 사용자 대상 빠른 이미지 로딩 필요

**선택지**
1. 로컬 파일 시스템
2. AWS S3 단독
3. S3 + CloudFront

**결정 이유**
- ✅ **확장성**: 무제한 저장 공간
- ✅ **성능**: CDN 캐싱을 통한 빠른 전송
- ✅ **보안**: Pre-signed URL 지원
- ✅ **비용 절감**: CloudFront를 통한 저렴한 전송

**구현**
```java
@Service
public class S3Service {
    
    public String generatePresignedUploadUrl(String fileName, MediaDomain domain) {
        String key = buildMediaKey(domain, fileName);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("image/jpeg")
            .build();
            
        return presigner.presignPutObject(putObjectRequest).url().toString();
    }
    
    public String toCdnUrl(String mediaKey) {
        return cloudFrontUrl + "/" + mediaKey;
    }
}
```

---

## 7. 인프라 및 배포

### ADR-011: AWS EC2 + RDS + S3 인프라

**결정**: AWS 클라우드 인프라 활용

**상황**
- 안정적인 서비스 운영 필요
- 확장 가능한 인프라 구성

**선택지**
1. 온프레미스 서버
2. AWS 클라우드
3. 다른 클라우드 제공자

**결정 이유**
- ✅ **관리형 서비스**: RDS, S3 등 운영 부담 감소
- ✅ **프리티어 활용**: AWS 프리티어 제도 적극 활용
- ✅ **안정성**: 99.9% 가용성 보장
- ✅ **생태계**: 다양한 서비스 연동 가능
- ✅ **인프라 제어 유연성**: 인프라 전반에 대한 제어권 확보 가능

---

### ADR-012: Docker + GitHub Actions CI/CD

**결정**: 컨테이너 기반 배포 파이프라인

**상황**
- 빠르고 안정적인 배포 필요
- 환경 일관성 보장

**선택지**
1. 직접 서버 배포
2. Docker 컨테이너
3. Kubernetes

**결정 이유**
- ✅ **환경 일관성**: 개발/운영 환경 동일화
- ✅ **배포 속도**: 빠른 이미지 빌드 및 배포
- ✅ **롤백 용이성**: 이전 이미지로 빠른 복원
- ✅ **낮은 난이도**: K8s 대비 운영 및 구축 난이도 낮음
- ✅ **비용 최소화**: 제한적 무료로 운영 가능

---

## 8. 모니터링 및 로깅

### ADR-013: Spring Actuator + Prometheus + Grafana 기반 APM 구축

**결정**: 매트릭 시각화 APM 구축

**상황**
- 애플리케이션 및 DB 상태 모니터링 필요
- 성능 병목 구간 식별
- 장애 발생 시 신속한 원인 파악

**선택지**
1. 커스텀 헬스체크
2. Spring Actuator
3. 외부 모니터링 도구

**결정 이유**
- ✅ **데이터 기반 의사결정**: 정량적 데이터를 통한 성능 개선 및 문제 해결 추구
- ✅ **장애 대응 시간 단축**: 장애 발생 시 신속한 원인 파악
- ✅ **비용 효율성 및 유연성**: 상용 APM 대비 저렴 및 커스텀 가능

---

### ADR-014: P6Spy + Slf4J

**결정**: 애플리케이션과 DB 로깅

**상황**
- 애플리케이션 레벨 로깅 요구
- 실제 JPA 쿼리 파악

**선택지**
1. Spring Boot의 show-sql, format_sql
2. P6Spy + Slf4J

**결정 이유**
- ✅ **파라미터 바인딩 명시**: 실제 바인딩 값 확인 가능
- ✅ **포맷팅**: 로깅 포맷을 커스텀하여 선택적 확인 가능

---

## 9. 성능 최적화

### ADR-015: JPA 성능 최적화 전략

**결정**: 다층 최적화 전략 적용

**최적화 방법**

1. **연관관계 최적화**
```java
// N+1 문제 해결
@Query("SELECT v FROM Vendor v JOIN FETCH v.region LEFT JOIN FETCH v.logoMedia")
List<Vendor> findVendorsWithRegionAndLogo();
```

2. **인덱스 설계**
```java
@Table(indexes = {
    @Index(name = "idx_region_level", columnList = "level"),
    @Index(name = "idx_vendor_type_region", columnList = "vendorType, region_id")
})
```

3. **쿼리 최적화**
```java
// QueryDSL 프로젝션 활용
.select(Projections.constructor(VendorWithMinPrice.class,
    vendor, weddingHall.basePrice.min()))
.groupBy(vendor.id)
.orderBy(weddingHall.basePrice.min().asc())
```

---

## 10. 확장성 고려사항

### ADR-016: 향후 확장 계획

**마이크로서비스 전환 준비**
- 도메인별 패키지 분리 완료
- 독립적인 데이터베이스 스키마 설계
- API Gateway 도입 고려

**기능 확장 로드맵**
1. **실시간 알림**: WebSocket 또는 SSE 도입
2. **결제 시스템**: 포트원(아임포트) 연동
3. **모바일 앱**: React Native 또는 Flutter

**성능 확장 계획**
1. **인스턴스**: Auto Scaling 도입
2. **데이터베이스**: Flyway DB 마이그레이션 툴 도입
3. **캐싱**: Redis 클러스터 구축
4. **검색**: Elasticsearch 도입
5. **CI/CD**: 무중단 그린/블루 배포 도입

---

**작성자**: 오현우, 김현빈
**버전**: 1.0.1