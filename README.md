# 🎉 ONMOIM - 온라인 모임 플랫폼

> 사람들이 쉽게 모임을 만들고 참여할 수 있는 온라인 모임 플랫폼

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)

---

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [API 문서](#-api-문서)
- [브랜치 전략](#-브랜치-전략)
- [팀원 정보](#-팀원-정보)
- [시작하기](#-시작하기)

---

## 🎯 프로젝트 소개

ONMOIM은 사용자들이 온라인으로 모임을 생성하고 참여할 수 있는 플랫폼입니다.
행사 생성부터 참여자 관리, 댓글 기능까지 모임 운영에 필요한 모든 기능을 제공합니다.

### 주요 특징
- 🔐 JWT 기반 안전한 인증 시스템
- 📧 이메일 인증을 통한 회원가입
- 🎪 행사 생성 및 관리
- 👥 참여자 투표 시스템
- 💬 실시간 댓글 기능
- 🖼️ Oracle Bucket 기반 이미지 업로드

---

## ✨ 주요 기능

### 👤 회원 관리
- 이메일 기반 회원가입 및 로그인
- JWT Access/Refresh Token 인증
- 프로필 조회 및 수정
- 프로필 이미지 업로드

### 🎪 행사 관리
- 행사 생성 (초안 → 발행)
- 행사 정보 수정 및 삭제
- 행사 목록 조회 (전체/내가 만든/내가 참여한)
- 행사 상세 조회
- 행사 썸네일 이미지 업로드

### 🗳️ 참여 관리
- 행사 참여 의사 투표 (참여/고민중)
- 참여자 목록 조회
- 참여 상태 변경

### 💬 댓글 기능
- 행사별 댓글 작성
- 댓글 목록 조회 (커서 기반 페이징)
- 댓글 삭제

---

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.5.9
- **Language**: Java 17
- **Build Tool**: Gradle
- **ORM**: Spring Data JPA
- **Security**: Spring Security + JWT

### Database
- **Main DB**: MySQL 8.0
- **Cache**: Redis

### Storage
- **Object Storage**: Oracle Cloud

### Documentation
- **API Docs**: Swagger (SpringDoc OpenAPI)

### Infrastructure
- **CI/CD**: GitHub Actions
- **Container**: Docker

---

## 📁 프로젝트 구조

```
src/main/java/backend/onmoim/
├── domain/
│   ├── auth/              # 인증 관련
│   │   ├── controller/
│   │   ├── service/
│   │   └── dto/
│   ├── user/              # 회원 관리
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── event/             # 행사 관리
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── converter/
│   │   └── dto/
│   ├── comment/           # 댓글 기능
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   └── test/              # 테스트 API
│
├── global/
│   ├── config/            # 설정 (Security, Swagger, Redis, OCI)
│   ├── security/          # JWT 필터, 인증 처리
│   ├── utils/             # 유틸리티 (JWT, OCI, 랜덤 닉네임)
│   ├── common/            # 공통 응답, 에러 코드
│   ├── entity/            # BaseEntity
│   └── validation/        # 커스텀 Validation
│
└── OnmoimApplication.java
```

### 아키텍처 특징
- **Domain-Driven Design**: 도메인별로 패키지 분리
- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: 요청/응답 DTO 분리
- **Converter Pattern**: Entity ↔ DTO 변환 로직 분리

---

## 📚 API 문서

### Swagger UI
```
https://onmoim.site/swagger-ui/index.html
```

### 주요 API 엔드포인트

#### 인증
- `POST /api/v1/users/signup` - 회원가입
- `POST /api/v1/users/login` - 로그인
- `POST /api/v1/auth/refresh` - 토큰 갱신

#### 회원
- `GET /api/v1/users` - 내 프로필 조회
- `PATCH /api/v1/users` - 프로필 수정
- `POST /api/v1/users/profile-image` - 프로필 이미지 업로드

#### 행사
- `POST /api/v1/users/events` - 행사 초안 생성
- `PATCH /api/v1/users/events/{eventId}` - 행사 수정
- `POST /api/v1/users/events/{eventId}/published` - 행사 발행
- `GET /api/v1/users/events` - 전체 행사 조회
- `GET /api/v1/users/events/{eventId}` - 행사 상세 조회
- `GET /api/v1/users/events/hosted` - 내가 만든 행사
- `GET /api/v1/users/events/participating` - 내가 참여한 행사
- `POST /api/v1/users/events/{eventId}/image` - 행사 이미지 업로드
- `DELETE /api/v1/users/events/{eventId}` - 행사 삭제

#### 참여
- `POST /api/v1/users/events/{eventId}/participants` - 참여 투표
- `GET /api/v1/users/events/{eventId}/participants` - 참여자 목록

#### 댓글
- `POST /api/v1/events/{eventId}/comments` - 댓글 작성
- `GET /api/v1/events/{eventId}/comments` - 댓글 목록 조회
- `DELETE /api/v1/comments/{commentId}` - 댓글 삭제

---

## 🌿 브랜치 전략

### Git Flow 기반 브랜치 전략

```
main (프로덕션)
  ↑
develop (개발)
  ↑
feature/* (기능 개발)
fix/* (버그 수정)
refactor/* (리팩토링)
```

### 브랜치 네이밍 규칙
- `feat/{기능명}` - 새로운 기능 개발
- `fix/{버그명}` - 버그 수정
- `refactor/{대상}` - 코드 리팩토링
- `docs/{문서명}` - 문서 수정
- `chore/{작업명}` - 기타 작업

### 커밋 메시지 규칙
```
[Type] 제목

- 변경 사항 1
- 변경 사항 2
```

**Type 종류**
- `Feat`: 새로운 기능 추가
- `Fix`: 기능 수정
- `Bug`: 버그 수정
- `Refactor`: 코드 리팩토링
- `Docs`: 문서 수정
- `Chore`: 기타 변경사항

자세한 내용은 문서 하단의 [Commit & Branch Convention](#-commit-message--branch-convention)을 참고하세요.

---

## 👥 팀원 정보

| 이름 | 역할 | GitHub | 담당 기능 |
|------|------|--------|-----------|
| 샤론/이재용 | Backend | [@Sharon0320](https://github.com/Sharon0320) | 회원 |
| 튜나/이동원 | Backend | [@tuna1025](https://github.com/tuna1025) | 이메일 |
| 쿠파/김한결 | Backend | [@koreanpaste](https://github.com/koreanpaste) | 분석 |
| 시나/황서린 | Backend | [@seorin05](https://github.com/seorin05) | 행사 |
| 곽철용/곽영찬 | Backend | [@YounchanHa](https://github.com/YounchanHa) | 행사 |


---

## 🚀 시작하기

### 필수 요구사항
- Java 17
- MySQL 8.0
- Redis
- Oracle Cloud Bucket (또는 S3 호환 스토리지)

### 환경 변수 설정

`.env` 파일 또는 환경 변수로 다음 값들을 설정하세요:

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/onmoim
DB_USER=your_username
DB_PW=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JWT
JWT_SECRET_KEY=your_secret_key_here

# Mail (Gmail)
MAIL_USERNAME_1=your_email@gmail.com
MAIL_PASSWORD_1=your_app_password

# OCI Bucket
OCI_URL=https://oraclecloud.com
OCI_ACCESS_KEY=your_access_key
OCI_SECRET_KEY=your_secret_key
OCI_BUCKET=bucket_name

# Cloudflare Turnstile
TURNSTILE_SECRET_KEY=your_turnstile_secret
TEST_TURNSTILE_TOKEN=test_token
```

### 실행 방법

1. **저장소 클론**
```bash
git clone https://github.com/your-org/ONMOIM_Server.git
cd ONMOIM_Server
```

2. **의존성 설치 및 빌드**
```bash
./gradlew build
```

3. **애플리케이션 실행**
```bash
./gradlew bootRun
```

4. **Swagger UI 접속**
```
http://localhost:8080/swagger-ui/index.html
```

### Docker로 실행

```bash
docker build -t onmoim-server .
docker run -p 8080:8080 \
  -e DB_URL=your_db_url \
  -e DB_USER=your_user \
  -e DB_PW=your_password \
  onmoim-server
```

---

## 📝 라이선스

This project is licensed under the MIT License.

---

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.

---

# 🧭 Commit Message & Branch Convention

팀의 협업 효율을 높이고, 변경 이력을 명확하게 관리하기 위한 커밋 메시지 및 브랜치 전략 가이드라인입니다.  
모든 커밋은 **한 가지 변경 사항만** 포함하며, 명확하고 일관된 메시지를 유지해주세요.

---

## 📘 Commit Message Convention

### 1. 커밋 유형 지정

커밋 메시지는 아래 유형 중 하나를 선택해 **첫 글자를 대문자**로 작성합니다.

| 커밋 유형 | 설명                                        |
|------------|-------------------------------------------|
| **Feat** | 새로운 기능 추가                                 |
| **Fix** | 기능 수정                                     |
| **Bug** | 버그 수정                                     |
| **Docs** | 문서 수정                                     |
| **Style** | 코드 포맷 변경, 세미콜론 누락 등 (코드 로직 변경 없음)         |
| **Refactor** | 코드 리팩토링                                   |
| **Test** | 테스트 코드 추가 또는 수정                           |
| **Chore** | 기타 변경 (예: 빌드 설정, 패키지 매니저 수정, .gitignore 등) |
| **Design** | CSS 등 UI 디자인 변경                           |
| **Comment** | 주석 추가 또는 변경                               |
| **Rename** | 파일/폴더 명 변경 (이동 포함)                        |
| **Remove** | 파일 삭제                                     |
| **!BREAKING CHANGE** | 대규모 API 변경                                |
| **!HOTFIX** | 급한 치명적 버그 수정                              |

---

### 2. 커밋 메시지 구조

```
[Type] 제목

- 변경 사항 1
- 변경 사항 2
- 변경 사항 3
```

#### 작성 규칙
- 제목은 **한글로**, 팀원이 내용을 바로 이해할 수 있게 작성합니다.  
- 제목은 **영문 기준 50자 이내**, 끝에는 마침표 `.` 사용하지 않습니다.  
- 제목의 **첫 글자는 대문자**로 작성합니다.  
- 본문에는 **무엇을 변경했는지와 그 이유**를 설명합니다 (어떻게보다는 "무엇 & 왜").  
- 여러 내용을 담을 경우 **글머리 기호( - )**로 구분하여 가독성을 높입니다.

#### 예시
```
[Feat] 회원가입 기능 추가

- 회원가입 API 및 서비스 로직 구현
- 유효성 검사 로직 추가
- 비밀번호 암호화 로직 추가
```

---

### 3. CLI에서 여러 줄 커밋 메시지 작성법

터미널에서 여러 줄 메시지를 직접 입력할 수 있습니다.

```bash
git commit -m "[Feat] 회원가입 기능 추가

- 회원가입 기능 추가
- 유효성 검사 로직 추가"
```

💡 쌍따옴표(`"`)를 닫지 않고 **개행 후 본문 작성**, 마지막 줄에서 닫으면 됩니다.

---

### 4. 커밋 메시지 작성이 중요한 이유

- 🧑‍🤝‍🧑 **팀원 간 원활한 소통**  
  어떤 변경이 이루어졌는지 쉽게 파악할 수 있습니다.
- ⏪ **과거 변경 사항 추적 용이**  
  특정 기능이나 버그 수정 내역을 빠르게 찾아볼 수 있습니다.
- 💼 **실무 협업 습관화**  
  올바른 커밋 관리 습관을 익혀 실무 프로젝트에서도 바로 적용할 수 있습니다.

---

### 5. 주의사항

- 한 커밋에는 **하나의 변경 사항**만 포함합니다.  
  (여러 수정이 포함되면 추적이 어려워집니다.)
- 불필요하거나 관련 없는 파일은 함께 커밋하지 않습니다.  
- API 엔드포인트, 서버 주소 등 **민감한 동적 정보**는 업로드 금지합니다.  
- **임시 파일**(`.log`, `.tmp`, 등)은 절대 업로드하지 않습니다.

---

## 🌿 Branch Convention

브랜치 이름은 기능 단위를 기준으로 생성하며, 모든 브랜치는 **소문자**로 작성합니다.

### 1. 기본 규칙

| 구분 | 설명 |
|------|------|
| **main / develop** | 직접 푸시 금지 → 반드시 PR(Merge Request)로 병합 |
| **push --force** | 금지 |
| **공백** | `_` (언더바)로 대체 |
| **이슈 단위 브랜치** | 하나의 기능 또는 버그 수정 단위만 포함 |
| **브랜치 종료** | 기능 완료 후 merge 시 삭제 |

---

### 2. 브랜치 네이밍 규칙

```
{type}/{short_description}
```

| Prefix | 설명 |
|---------|------|
| **feat/** | 새로운 기능 개발 |
| **fix/** | 버그 수정 |
| **refactor/** | 코드 리팩토링 |
| **design/** | UI/UX 관련 개선 |
| **docs/** | 문서 수정 |
| **test/** | 테스트 코드 관련 작업 |
| **chore/** | 설정 파일 등 기타 작업 |

#### 예시
```
feat/login
fix/oauth_login
refactor/user_service
design/header_layout
```

---

**한 커밋 = 한 작업 단위**  
작업의 추적 가능성과 협업 효율을 높이기 위해 유지해주세요.
