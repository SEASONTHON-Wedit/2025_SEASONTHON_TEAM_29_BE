# 🎀 Wedit - 웨딩 플래닝 서비스 백엔드

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-orange?style=for-the-badge&logo=amazon-aws&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker&logoColor=white" />
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
### 🎯 핵심 가치

- **📅 통합 예약 관리**: 웨딩홀, 드레스, 스튜디오, 메이크업 업체 예약을 한 번에
- **👫 커플 연동**: 신랑신부가 함께 웨딩 플랜을 관리
- **📝 리뷰 시스템**: 실제 이용 후기를 통한 신뢰성 있는 정보 제공
- **💰 견적 관리**: 각 업체별 견적 비교 및 관리

---
## 🪾 브랜치 전략
- **Issue**를 통한 feature/bug/fix/refactor 브랜치 생성

---

## 🏛️ 아키텍처 의사결정 기록 (ADR)

본 프로젝트의 주요 아키텍처 및 기술적 의사결정 과정을 상세히 문서화하였습니다.

- **[➡️ 아키텍처 의사결정 기록 (ADR) 전문](./ADR.md)**

---

## 📁 프로젝트 구조

```
src/main/java/com/wedit/backend/
├── BackendApplication.java
├── api/                              # API 컨트롤러
│   ├── member/                       # 👤 회원 관련
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── jwt/
│   │   │   ├── entity/
│   │   │   ├── filter/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── repository/
│   │   └── service/
│   ├── vendor/                       # 🏢 업체 관련
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── reservation/                  # 📅 예약 관련
│   ├── review/                       # ⭐ 리뷰 관련
│   ├── contract/                     # 📄 계약 관련
│   ├── tour/                         # 🎯 투어 관련
│   ├── estimate/                     # 💰 견적 관련
│   └── aws/s3/                       # 📁 파일 업로드
└── common/                           # 🛠 공통 설정
    ├── config/                       # 설정 클래스
    ├── security/                     # 보안 설정
    ├── oauth2/                       # OAuth2 설정
    ├── exception/                    # 예외 처리
    └── response/                     # 응답 포맷
```

---

## 🔗 API 문서

### Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

### API Docs JSON
```
http://localhost:8080/api/v3/api-docs
```

---

## 📊 ERD

<img width="6436" height="7096" alt="wedit_ERD" src="https://github.com/user-attachments/assets/f21a976e-3781-4f80-b5e8-fea258dae945" />

---

## 🏗 인프라 아키텍처

<img width="1085" height="813" alt="infra drawio2" src="https://github.com/user-attachments/assets/91c247e5-b878-41c7-af6f-1b0384b0e60b" />

---

## 👥 팀원

### Backend 개발팀

| 이름      | 역할 | GitHub                                         | 담당 업무                  |
|---------|------|------------------------------------------------|------------------------|
| **김현빈** | Backend Lead | [@Wien0128](https://github.com/wien0128)   | 아키텍처 설계, 인증/인가, CI/CD, API 개발 |
| **오현우** | Backend Developer | [@HyunWoo9930](https://github.com/HyunWoo9930) | API 개발, 소셜로그인, 검색          |

<div align="center">

**🎉 Wedit과 함께 완벽한 웨딩을 준비하세요! 🎉**

[![Website](https://img.shields.io/badge/Website-wedit.me-blue?style=for-the-badge)](https://wedit.me)
[![API Documentation](https://img.shields.io/badge/API%20Docs-Swagger-green?style=for-the-badge)](https://wedit.me/api/swagger-ui.html)

</div>
