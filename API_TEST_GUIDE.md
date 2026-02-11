# API 테스트 가이드

## 인증 방법

이 API는 JWT 기반 인증을 사용합니다. 아래 두 가지 방법 중 하나를 선택하여 테스트할 수 있습니다.

---

## 방법 1: 테스트 계정으로 로그인 (권장)

### 1단계: 회원가입
```bash
POST /api/v1/users/signup
Content-Type: application/json

{
  "email": "test@example.com",
  "authCode": "123456"
}
```

### 2단계: 로그인
```bash
POST /api/v1/users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "authCode": "123456"
}
```

**응답 예시:**
```json
{
  "success": true,
  "code": "REQUEST_200",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "test@example.com",
    "nickname": "랜덤닉네임"
  }
}
```

### 3단계: API 호출 시 토큰 사용
```bash
GET /api/v1/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 방법 2: 마스터 JWT 생성 (개발/테스트 전용)

기존 사용자 계정으로 긴 유효기간의 JWT를 생성합니다.

### 1단계: 마스터 JWT 생성
```bash
POST /api/v1/test/master-jwt?email=test@example.com
```

**응답 예시:**
```json
{
  "success": true,
  "code": "REQUEST_200",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "1",
    "email": "test@example.com",
    "nickname": "테스트유저",
    "message": "이 토큰을 Authorization 헤더에 'Bearer {token}' 형식으로 사용하세요"
  }
}
```

### 2단계: 생성된 토큰 사용
```bash
GET /api/v1/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 토큰 검증

현재 토큰이 유효한지 확인:

```bash
GET /api/v1/test/me
Authorization: Bearer {your-token}
```

**응답 예시:**
```json
{
  "success": true,
  "code": "REQUEST_200",
  "message": "요청이 성공했습니다.",
  "data": {
    "userId": 1,
    "email": "test@example.com",
    "nickname": "테스트유저",
    "status": "ACTIVE"
  }
}
```

---

## Swagger UI에서 테스트

1. Swagger UI 접속: `http://localhost:8080/swagger-ui/index.html`
2. 우측 상단 "Authorize" 버튼 클릭
3. Value 입력란에 `Bearer {your-token}` 입력
4. "Authorize" 클릭
5. 이제 모든 API를 테스트할 수 있습니다

---

## 주의사항

⚠️ **보안 경고**
- `/api/v1/test/**` 엔드포인트는 개발/테스트 환경에서만 사용하세요
- 프로덕션 환경에서는 반드시 비활성화해야 합니다
- 마스터 JWT는 외부에 노출되지 않도록 주의하세요

---

## 토큰 만료 시간

- **Access Token**: 1시간 (3600000ms)
- **Refresh Token**: 7일 (604800000ms)

토큰이 만료되면 `/api/v1/auth/refresh` 엔드포인트로 갱신할 수 있습니다.
