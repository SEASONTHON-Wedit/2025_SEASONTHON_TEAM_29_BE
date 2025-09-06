# 🎀 Wedit - 웨딩 플래닝 서비스 백엔드

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-orange?style=for-the-badge&logo=amazon-aws&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker&logoColor=white" />
</div>

<br />

**2025 Kakao X Goorm 시즌톤 29팀 "Wedit" 백엔드 레포지토리**

> 🎊 **예비 신랑신부를 위한 올인원 웨딩 플래닝 플랫폼**  
> 웨딩홀, 드레스, 스튜디오, 메이크업 등 웨딩 관련 업체 예약 및 관리 서비스

---

## 📋 목차

- [🚀 프로젝트 소개](#-프로젝트-소개)
- [🛠 기술 스택](#-기술-스택)
- [🏗 프로젝트 아키텍처](#-프로젝트-아키텍처)
- [🌟 주요 기능](#-주요-기능)
- [📁 프로젝트 구조](#-프로젝트-구조)
- [🔗 API 문서](#-api-문서)
- [⚙️ 환경 설정](#️-환경-설정)
- [🚀 실행 방법](#-실행-방법)
- [🐳 Docker 실행](#-docker-실행)
- [📊 데이터베이스 구조](#-데이터베이스-구조)
- [🔐 보안 및 인증](#-보안-및-인증)
- [📝 API 엔드포인트](#-api-엔드포인트)
- [🧪 테스트](#-테스트)
- [🚀 배포](#-배포)
- [👥 팀원](#-팀원)

---

## 🚀 프로젝트 소개

**Wedit**은 예비 신랑신부들이 웨딩 준비 과정에서 필요한 모든 서비스를 한 곳에서 관리할 수 있는 플랫폼입니다.

### 🎯 핵심 가치

- **📅 통합 예약 관리**: 웨딩홀, 드레스, 스튜디오, 메이크업 업체 예약을 한 번에
- **👫 커플 연동**: 신랑신부가 함께 웨딩 플랜을 관리
- **📝 리뷰 시스템**: 실제 이용 후기를 통한 신뢰성 있는 정보 제공
- **💰 견적 관리**: 각 업체별 견적 비교 및 관리

---

## 🏗 프로젝트 아키텍처

<img width="552" height="355" alt="Image" src="https://github.com/user-attachments/assets/eb527460-b91a-4842-a73a-c89718061444" />

---

## 🌟 주요 기능

### 👤 회원 관리
- **일반 회원가입/로그인** - 이메일 기반 계정 시스템
- **소셜 로그인** - Google, Naver 연동
- **SMS 인증** - 전화번호 본인인증
- **커플 연동** - 신랑신부 계정 연결 시스템

### 🏢 업체 관리
- **카테고리별 업체 조회**
    - 웨딩홀 (WEDDING_HALL)
    - 드레스 (DRESS)
    - 스튜디오 (STUDIO)
    - 메이크업 (MAKEUP)
- **상세 정보 관리** - 업체별 특화 정보 저장
- **이미지 업로드** - AWS S3 연동 파일 관리
- **위치 기반 검색** - 주소 정보 관리

### 📅 예약 시스템
- **실시간 예약 가능 시간 조회**
- **예약 생성 및 관리**
- **예약 상태 추적**
- **예약 충돌 방지 로직**

### 📝 리뷰 시스템
- **이용 후기 작성/수정/삭제**
- **별점 평가 시스템**
- **리뷰 통계 제공**
- **이미지 첨부 기능**

### 🎯 투어 관리
- **업체 견학 일정 관리**
- **투어 예약 시스템**
- **투어 후기 관리**

### 💰 견적 시스템
- **업체별 견적 요청**
- **견적 비교 기능**
- **견적서 관리**

---

## 📁 프로젝트 구조


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

----

## 📊 데이터베이스 구조

<img width="4236" height="3424" alt="Image" src="https://github.com/user-attachments/assets/cc7fc5a7-3816-4c4d-b494-77043e703102" />
----

## 👥 팀원

### Backend 개발팀

| 이름 | 역할 | GitHub | 담당 업무 |
|------|------|--------|----------|
| **김현빈** | Backend Lead | [@kimhyunbin](https://github.com/kimhyunbin) | 아키텍처 설계, 인증 시스템, CI/CD |
| **팀원2** | Backend Developer | [@teammate2](https://github.com/teammate2) | API 개발, 데이터베이스 설계 |
| **팀원3** | Backend Developer | [@teammate3](https://github.com/teammate3) | 외부 API 연동, 테스트 코드 |


<div align="center">

**🎉 Wedit과 함께 완벽한 웨딩을 준비하세요! 🎉**

[![Website](https://img.shields.io/badge/Website-wedit.me-blue?style=for-the-badge)](https://wedit.me)
[![API Documentation](https://img.shields.io/badge/API%20Docs-Swagger-green?style=for-the-badge)](https://wedit.me/api/swagger-ui.html)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-black?style=for-the-badge&logo=github)](https://github.com/your-username/2025_SEASONTHON_TEAM_29_BE)

</div>