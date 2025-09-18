# 🎀 Wedit - 웨딩 플래닝 서비스 백엔드

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.1-blue?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-orange?style=for-the-badge&logo=amazon-aws&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/QueryDSL-5.0.0-purple?style=for-the-badge&logo=gradle&logoColor=white" />
    <a href="./ADR.md"><img src="https://img.shields.io/badge/ADR-active-999999?style=for-the-badge" /></a>
</div>

<br />

**2025 Kakao X Goorm 시즌톤 29팀 "Wedit" 백엔드 레포지토리**

> 🎊 **예비 신랑신부를 위한 올인원 웨딩 플래닝 플랫폼**  
> 웨딩홀, 드레스, 스튜디오, 메이크업 등 웨딩 관련 업체 예약 및 관리 서비스

---

## 🚀 프로젝트 소개

**Wedit**은 예비 신랑신부들이 웨딩 준비 과정에서 필요한 모든 서비스를 한 곳에서 관리할 수 있는 플랫폼입니다.

<table align="center">
  <tr>
    <td><img width="400" alt="Image 2" src="https://github.com/user-attachments/assets/2858f4a6-6bc5-46db-9509-c291120a7783" /></td>
    <td><img width="400" alt="Image 1" src="https://github.com/user-attachments/assets/97266e71-5c60-4a34-970d-97db7f53e374" /></td>
  </tr>
</table>

---

### 🎯 핵심 기능

- **👥 회원 관리**: JWT 기반 인증, OAuth2 소셜 로그인 (카카오, 구글, 네이버)
- **👫 커플 연동**: 신랑신부가 함께 웨딩 플랜을 관리할 수 있는 커플 코드 시스템
- **🏢 업체 관리**: 웨딩홀, 드레스, 스튜디오, 메이크업 업체 정보 및 상품 관리
- **📅 예약 시스템**: 실시간 예약 가능 시간 조회 및 예약 관리
- **📄 계약 관리**: 업체와의 계약 및 견적서 관리
- **⭐ 리뷰 시스템**: 실제 이용 후기를 통한 신뢰성 있는 정보 제공
- **🛒 장바구니**: 여러 업체 서비스를 한 번에 관리
- **📝 투두리스트**: 웨딩 준비 체크리스트 관리
- **🎨 모바일 청첩장**: 개인화된 모바일 청첩장 생성
- **📊 캘린더**: 웨딩 관련 일정 통합 관리
- **📱 SMS 인증**: CoolSMS를 이용한 본인 인증

---

## 🛠 기술 스택

### **Core Framework**
- **Spring Boot 3.5.5** - 메인 프레임워크
- **Java 21** - 최신 LTS 버전

### **Database & ORM**
- **MySQL 8.1** - 메인 데이터베이스
- **Spring Data JPA** - 데이터 접근 계층
- **QueryDSL 5.0.0** - 타입 안전한 동적 쿼리
- **H2** - 테스트용 인메모리 데이터베이스

### **Security & Authentication**
- **Spring Security** - 보안 프레임워크
- **JWT (JJWT 0.11.5)** - 토큰 기반 인증
- **OAuth2 Client** - 소셜 로그인 (카카오, 구글, 네이버)

### **Cloud & Infrastructure**
- **AWS EC2** - 서버 호스팅
- **AWS RDS** - 관리형 MySQL 데이터베이스
- **AWS S3** - 파일 저장소
- **AWS CloudFront** - CDN Caching
- **Docker** - 컨테이너화
- **Cloudflare** - DNS, TSL/SSL 인증, Caching

### **External Services**
- **CoolSMS** - SMS 인증 서비스
- **AWS SDK S3** - S3 PreSigned URL 생성

### **Monitoring & Documentation**
- **SpringDoc OpenAPI (Swagger)** - API 문서화
- **Spring Actuator** - 애플리케이션 모니터링
- **Micrometer Prometheus** - 메트릭 수집
- **Grafana** - RDS, Spring 모니터링 대시보드 구성
- **P6Spy** - SQL 로깅

### **Build & DevOps**
- **Gradle** - 빌드 도구
- **GitHub Actions** - CI/CD 파이프라인
- **Docker Hub** - 컨테이너 이미지 저장소

### **Testing & Development**
- **JUnit 5** - 단위 테스트
- **Spring Boot Test** - 통합 테스트
- **Lombok** - 코드 자동 생성
- **Spring DevTools** - 개발 편의성

---

## 🪾 개발 워크플로우

### **브랜치 전략**
- **Issue 기반 개발**: GitHub Issue를 통한 작업 관리
- **feature/**, **bug/**, **fix/**, **refactor/** 브랜치 생성 및 관리
- **Pull Request** 기반 코드 리뷰

### **CI/CD 파이프라인**
1. **CI (Continuous Integration)**
   - GitHub Actions를 통한 자동화된 빌드
   - Gradle 의존성 캐싱으로 빌드 시간 단축
   - Docker 이미지 생성 및 DockerHub 푸시

2. **CD (Continuous Deployment)**
   - CI 성공 시 자동으로 EC2에 배포
   - DockerHub에서 최신 이미지 풀링
   - Spring Boot Docker 컨테이너 실행
   
---

## 🏛️ 아키텍처 의사결정 기록 (ADR)

본 프로젝트의 주요 아키텍처 및 기술적 의사결정 과정을 상세히 문서화하였습니다.

- **[➡️ 아키텍처 의사결정 기록 (ADR) 전문](./ADR.md)**

---

## 📁 프로젝트 구조

```
src/main/java/com/wedit/backend/
├── BackendApplication.java                # 메인 애플리케이션 클래스
├── api/                                   # 도메인별 API 계층
│   ├── member/                           # 👤 회원 관리
│   │   ├── controller/                   # REST 컨트롤러
│   │   ├── dto/                          # 데이터 전송 객체
│   │   ├── entity/                       # JPA 엔티티
│   │   ├── repository/                   # 데이터 액세스 계층
│   │   ├── service/                      # 비즈니스 로직
│   │   └── jwt/                          # JWT 인증 관련
│   │       ├── entity/                   # JWT 엔티티 (RefreshToken)
│   │       ├── filter/                   # JWT 필터
│   │       ├── repository/               # JWT 저장소
│   │       └── service/                  # JWT 서비스
│   ├── vendor/                           # 🏢 업체 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   │   ├── request/                  # 요청 DTO
│   │   │   └── response/                 # 응답 DTO
│   │   ├── entity/
│   │   │   └── enums/                    # 업체 타입 열거형
│   │   ├── repository/
│   │   └── service/
│   ├── reservation/                      # 📅 예약 시스템
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                       # 예약, 상담 슬롯 엔티티
│   │   ├── repository/
│   │   └── service/
│   ├── contract/                         # 📄 계약 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── review/                           # ⭐ 리뷰 시스템
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── cart/                             # 🛒 장바구니
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                       # 장바구니, 장바구니 아이템
│   │   ├── repository/
│   │   └── service/
│   ├── todoList/                         # 📝 투두리스트
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                       # 투두 템플릿, 회원 투두
│   │   ├── repository/
│   │   └── service/
│   ├── invitation/                       # 🎨 모바일 청첩장
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                       # 청첩장 구성 요소들
│   │   ├── repository/
│   │   └── service/
│   ├── calendar/                         # 📊 캘린더
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                       # 사용자/관리자 이벤트
│   │   ├── repository/
│   │   └── service/
│   ├── tour/                             # 🎯 드레스 투어 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── media/                            # 📁 미디어 관리
│   │   ├── dto/
│   │   ├── entity/
│   │   │   └── enums/                    # 미디어 타입, 도메인
│   │   ├── repository/
│   │   └── service/
│   └── aws/s3/                           # ☁️ AWS S3 파일 업로드
│       ├── controller/
│       ├── dto/                          # PreSigned URL DTO
│       ├── service/
│       └── util/                         # 미디어 유틸리티
└── common/                               # 🛠 공통 컴포넌트
    ├── config/                           # 설정 클래스
    │   ├── security/                     # Spring Security 설정
    │   ├── jwt/                          # JWT 설정
    │   ├── swagger/                      # Swagger 설정
    │   ├── queryDSL/                     # QueryDSL 설정
    │   └── aws/s3/                       # AWS S3 설정
    ├── entity/                           # 공통 엔티티
    │   ├── BaseTimeEntity.java           # 생성/수정 시간 기본 엔티티
    │   └── ExtendBaseTimeEntity.java     # 확장 시간 엔티티
    ├── oauth2/                           # OAuth2 설정
    │   ├── OAuth2UserService.java        # 커스텀 OAuth2 사용자 서비스
    │   ├── OAuthAttributes.java          # OAuth 속성 매핑
    │   └── OAuth2AuthenticationHandler   # 인증 성공/실패 핸들러
    ├── response/                         # 공통 응답 형식
    │   ├── ApiResponse.java              # 표준 API 응답
    │   ├── SuccessStatus.java            # 성공 상태 코드
    │   └── ErrorStatus.java              # 에러 상태 코드
    ├── exception/                        # 예외 처리
    │   ├── BaseException.java            # 기본 예외 클래스
    │   ├── BadRequestException.java
    │   ├── UnauthorizedException.java
    │   ├── ForbiddenException.java
    │   ├── NotFoundException.java
    │   ├── ConflictException.java
    │   └── InternalServerException.java
    ├── advice/                           # 전역 예외 처리
    │   └── GlobalExceptionAdvice.java
    ├── event/                            # 도메인 이벤트
    │   ├── ReservationCreatedEvent.java  # 예약 생성 이벤트
    │   └── ReservationCancelledEvent.java # 예약 취소 이벤트
    ├── data/                             # 데이터 초기화
    │   └── RegionDataInitializer.java
    └── util/                             # 유틸리티
        └── PerformanceTimer.java         # 성능 측정 유틸
```

### **주요 아키텍처 특징**

1. **도메인 중심 설계**: 각 도메인별로 독립적인 패키지 구조
2. **계층화 아키텍처**: Controller → Service → Repository 계층 분리
3. **공통 컴포넌트 분리**: 재사용 가능한 공통 기능들을 별도 패키지로 관리
4. **이벤트 기반 아키텍처**: Spring Events를 활용한 도메인 간 느슨한 결합
5. **표준화된 응답 형식**: 일관된 API 응답 구조

---

## 🔗 API 문서

### **Swagger UI**
```
http://localhost:8080/api/swagger-ui.html
```

### **API Docs JSON**
```
http://localhost:8080/api/v3/api-docs
```

### **주요 API 엔드포인트**

| 도메인 | 엔드포인트 | 설명 |
|--------|------------|------|
| **회원** | `/api/member/**` | 로그인, 회원가입, 인증/인가 |
| **커플** | `/api/couple/**` | 커플 |
| **업체** | `/api/vendor/**` | 업체/상품 |
| **예약** | `/api/reservation/**` | 상담 예약 |
| **계약** | `/api/contract/**` | 계약 |
| **리뷰** | `/api/review/**` | 후기  |
| **장바구니** | `/api/cart/**` | 장바구니/아이템 |
| **투두** | `/api/todolist/**` | 웨딩 준비 체크리스트 |
| **청첩장** | `/api/invitation/**` | 모바일 청첩장 |
| **캘린더** | `/api/calendar/**` | 개인/커플 일정 |
| **드레스투어** | `/api/tour/**` | 드레스 투어 |
| **드레스로망** | `/api/tour-romance/**` | 드레스 로망 |

---

## 📊 ERD

<img width="6436" height="7096" alt="wedit_ERD" src="https://github.com/user-attachments/assets/f21a976e-3781-4f80-b5e8-fea258dae945" />

---

## 🏗 인프라 아키텍처

<img width="1085" height="813" alt="infra drawio2" src="https://github.com/user-attachments/assets/91c247e5-b878-41c7-af6f-1b0384b0e60b" />

### **아키텍처 구성**

1. **Load Balancer**: Nginx (Reverse Proxy)
2. **Application Server**: AWS EC2
3. **Database**: AWS RDS (MySQL)
4. **File Storage**: AWS S3 + CloudFront CDN
5. **CI/CD**: GitHub Actions + Docker Hub
6. **Monitoring**: Spring Actuator + Prometheus + Grafana
7. **DNS, TLS/SSL**: Cloudflare

---

## 🚀 시작하기

### **사전 요구사항**
- Java 21 이상
- MySQL 8.0 이상
- Docker (선택사항)

### **로컬 개발 환경 설정**

1. **프로젝트 클론**
   ```bash
   git clone https://github.com/2025-Kakao-Goorm-Seasonthon/2025_SEASONTHON_TEAM_29_BE.git
   cd 2025_SEASONTHON_TEAM_29_BE
   ```

2. **MySQL 데이터베이스 생성**
   ```sql
   CREATE DATABASE wedit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **환경 설정**
   ```bash
   # application.yml에서 데이터베이스 설정 확인
   # OAuth2, JWT, AWS 등 필요한 환경변수 설정
   ```

4. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

### **Docker를 이용한 실행**

```bash
# Docker 이미지 빌드
docker build -t wedit-backend .

# 컨테이너 실행
docker run -d -p 8080:8080 \
  -v wedit_uploads:/app/uploads \
  -v wedit_logs:/app/logs \
  wedit-backend
```

---

## 📈 모니터링

### **Spring Actuator 엔드포인트**
- **Health Check**: `/actuator/health`
- **Application Info**: `/actuator/info`

### **로깅**
- **로그 파일**: `logs/wedit.log`
- **로그 레벨**: INFO (운영), DEBUG (개발)
- **로그 보관**: 7일, 최대 파일 크기 10MB

---

## 👥 팀원

### **Backend 개발팀**

| 이름      | 역할 | GitHub                                         | 담당 업무                  |
|---------|------|------------------------------------------------|------------------------|
| **김현빈** | Backend Lead | [@Wien0128](https://github.com/wien0128)   | 아키텍처 설계, 인증/인가, CI/CD, 코어 API 개발 |
| **오현우** | Backend Developer | [@HyunWoo9930](https://github.com/HyunWoo9930) | 업체/예약 API 개발, 소셜로그인, 검색 기능 |

---

<div align="center">

**🎉 Wedit과 함께 완벽한 웨딩을 준비하세요! 🎉**

[![Website](https://img.shields.io/badge/Website-wedit.me-blue?style=for-the-badge)](https://wedit.me)
[![API Documentation](https://img.shields.io/badge/API%20Docs-Swagger-green?style=for-the-badge)](https://wedit.me/api/swagger-ui.html)

</div>
