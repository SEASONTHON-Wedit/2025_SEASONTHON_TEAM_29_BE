# Architecture Decision Record (ADR) - Wedit

> **웨딩 플래닝 서비스 아키텍처 의사결정 기록**  
> 작성일: 2025년 9월 15일  
> 작성자: Backend Development Team

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

**선택지**
1. 마이크로서비스 아키텍처
2. 모놀리식 아키텍처

**결정 이유**
- ✅ **개발 속도**: 빠른 프로토타이핑 가능
- ✅ **운영 복잡성 최소화**: 단일 배포 단위
- ✅ **팀 규모에 적합**: 적은 인원으로 관리 가능
- ✅ **통합 테스트 용이성**: 전체 시스템 테스트 가능

**결과**
- Spring Boot 단일 애플리케이션으로 구성
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

**결과**
```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

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
- ✅ **타입 안정성**: 컴파일 타임 오류 검출
- ✅ **가독성**: SQL과 유사한 직관적 문법
- ✅ **동적 쿼리**: BooleanBuilder로 조건부 쿼리 작성 용이
- ✅ **IDE 지원**: 자동완성 및 리팩토링 지원

**구현**
```java
@Repository
@RequiredArgsConstructor
public class VendorProductQueryRepository {
    private final JPAQueryFactory queryFactory;
    
    public List<VendorWithMinPrice> searchWeddingHallVendors(...) {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (regionCodes != null && !regionCodes.isEmpty()) {
            builder.and(vendor.region.code.in(regionCodes));
        }
        
        return queryFactory
            .select(Projections.constructor(VendorWithMinPrice.class, ...))
            .from(weddingHall)
            .where(builder)
            .fetch();
    }
}
```

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
3. MongoDB

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

// 최적화된 쿼리
@Query("SELECT r.code FROM Region r WHERE r.parent.code = :parentCode AND r.level = 3")
List<String> findLevel3CodesByParentCode(@Param("parentCode") String parentCode);
```

---

## 4. 보안 아키텍처

### ADR-006: JWT + OAuth2 하이브리드 인증

**결정**: JWT Access Token + OAuth2 소셜 로그인 조합

**상황**
- 웨딩 준비는 커플이 함께 하는 활동
- 간편한 회원가입 및 로그인 필요
- 보안성과 사용성 두 가지 모두 중요

**선택지**
1. 세션 기반 인증
2. JWT 단독
3. OAuth2 + JWT 조합

**결정 이유**
- ✅ **Stateless**: 서버 확장성 향상
- ✅ **소셜 로그인**: 사용자 진입 장벽 낮춤
- ✅ **토큰 관리**: Access/Refresh Token 분리
- ✅ **다중 플랫폼**: 웹/모바일 대응 가능

**구현**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler))
            .addFilterBefore(jwtAuthenticationProcessingFilter, 
                           UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### ADR-007: SMS 인증 시스템

**결정**: CoolSMS API 활용

**상황**
- 휴대폰 번호 기반 본인 인증 필요
- 예약 확인 및 알림 발송

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

**구현**
```java
@RestController
@RequestMapping("/api/v1/vendor")
@Tag(name = "Vendor", description = "업체 관련 API")
public class VendorController {
    
    @Operation(summary = "웨딩홀 검색", description = "조건별 웨딩홀 검색")
    @PostMapping("/wedding-hall/search")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchWeddingHall(
        @RequestBody WeddingHallSearchRequestDTO request) {
        // 구현
    }
}
```

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

**결정**: S3 저장 + CloudFront CDN 조합

**상황**
- 웨딩 업체 이미지, 포트폴리오 저장
- 전국 사용자 대상 빠른 이미지 로딩 필요

**선택지**
1. 로컬 파일 시스템
2. AWS S3 단독
3. S3 + CloudFront

**결정 이유**
- ✅ **확장성**: 무제한 저장 공간
- ✅ **성능**: CDN을 통한 빠른 전송
- ✅ **보안**: Pre-signed URL 지원
- ✅ **비용**: 사용량 기반 과금

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

### ADR-011: Docker + GitHub Actions CI/CD

**결정**: 컨테이너 기반 배포 파이프라인

**상황**
- 빠른 배포와 롤백 필요
- 환경 일관성 보장

**선택지**
1. 직접 서버 배포
2. Docker 컨테이너
3. Kubernetes

**결정 이유**
- ✅ **환경 일관성**: 개발/운영 환경 동일화
- ✅ **배포 속도**: 빠른 이미지 빌드 및 배포
- ✅ **롤백 용이성**: 이전 이미지로 빠른 복원
- ✅ **적정 복잡도**: K8s 대비 운영 부담 적음

**구현**
```dockerfile
FROM openjdk:21
WORKDIR /app
COPY ${JAR_FILE} wedit_backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wedit_backend.jar"]
```

```yaml
# CI/CD Pipeline
- name: Build Docker Image
  run: |
    docker build -t ${{ secrets.DOCKERHUB_USERNAME }}/wedit_cicd_action:${{ github.sha }} .
    
- name: Deploy to EC2
  run: |
    sudo docker pull ${{ secrets.DOCKERHUB_USERNAME }}/wedit_cicd_action:latest
    sudo docker run --name wedit -d -p 8080:8080 ...
```

### ADR-012: AWS EC2 + RDS + S3 인프라

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
- ✅ **확장성**: Auto Scaling 지원
- ✅ **안정성**: 99.9% 가용성 보장
- ✅ **생태계**: 다양한 서비스 연동 가능

---

## 8. 모니터링 및 로깅

### ADR-013: Spring Actuator + Logback

**결정**: 내장된 모니터링 도구 활용

**상황**
- 애플리케이션 상태 모니터링 필요
- 구조화된 로깅 시스템 구축

**선택지**
1. 커스텀 헬스체크
2. Spring Actuator
3. 외부 모니터링 도구

**결정 이유**
- ✅ **즉시 사용 가능**: 별도 구현 불필요
- ✅ **표준화**: Spring Boot 표준 방식
- ✅ **확장성**: 커스텀 지표 추가 가능
- ✅ **보안**: 엔드포인트 접근 제어 가능

**구현**
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info
        
logging:
  level:
    root: info
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/wedit.log
```

---

## 9. 성능 최적화

### ADR-014: JPA 성능 최적화 전략

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

### ADR-015: 캐싱 전략

**결정**: 애플리케이션 레벨 캐싱 (향후 확장)

**상황**
- 지역 데이터는 변경 빈도가 낮음
- 인기 업체 조회 빈도 높음

**향후 적용 예정**
```java
@Cacheable(value = "regions", key = "#level")
public List<Region> getRegionsByLevel(int level) {
    return regionRepository.findByLevel(level);
}
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
3. **추천 시스템**: 협업 필터링 알고리즘
4. **모바일 앱**: React Native 또는 Flutter

**성능 확장 계획**
1. **데이터베이스**: Read Replica 도입
2. **캐싱**: Redis 클러스터 구축
3. **검색**: Elasticsearch 도입
4. **CDN**: 글로벌 서비스 확장

---

## 결론

Wedit 프로젝트는 웨딩 플래닝 도메인의 특성을 고려하여 **안정성**, **개발 효율성**, **확장성**을 균형있게 고려한 아키텍처로 설계되었습니다.

### 핵심 성과
- ✅ **모던 기술 스택**: Java 21 + Spring Boot 3.x
- ✅ **클라우드 네이티브**: AWS 기반 인프라
- ✅ **보안 강화**: OAuth2 + JWT 인증 체계
- ✅ **성능 최적화**: QueryDSL + 계층형 데이터 최적화
- ✅ **운영 자동화**: Docker + CI/CD 파이프라인

### 교훈 및 개선점
1. **보안 강화**: 환경변수 관리 개선 필요
2. **테스트 커버리지**: 단위/통합 테스트 확대
3. **모니터링**: APM 도구 도입 고려
4. **문서화**: API 문서 지속적 업데이트

이 ADR은 프로젝트의 기술적 의사결정 과정을 기록하여, 향후 유지보수와 확장 시 참고 자료로 활용될 것입니다.

---

**작성자**: Backend Development Team  
**최종 수정**: 2025년 9월 15일  
**버전**: 1.0
