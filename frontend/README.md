# 야구 게임 프론트엔드

React 기반의 야구 게임 웹 애플리케이션입니다.

## 주요 기능

- **사용자 인증**: 로그인/회원가입/로그아웃
- **게시판**: KBO 관련 게시글 작성, 조회, 수정, 삭제
- **야구 게임**: 실시간 야구 게임 플레이
- **팀 관리**: 팀 정보 조회 및 설정
- **프로필**: 사용자 프로필 관리

## 기술 스택

- **Frontend**: React 18, React Router v6
- **HTTP Client**: Axios
- **Styling**: CSS3
- **State Management**: React Hooks

## 백엔드 연동

### API 엔드포인트

#### 인증 (Authentication)
- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/logout` - 로그아웃
- `POST /api/auth/refresh` - 토큰 갱신

#### 게시판 (Board)
- `GET /api/board` - 게시글 목록 조회
- `GET /api/board/{id}` - 게시글 상세 조회
- `POST /api/board` - 게시글 작성
- `PUT /api/board/{id}` - 게시글 수정
- `DELETE /api/board/{id}` - 게시글 삭제

#### 게임 (Game)
- `POST /api/game` - 게임 생성
- `GET /api/game/{id}` - 게임 상태 조회
- `PUT /api/game/{id}` - 게임 상태 업데이트
- `POST /api/game/{id}/end` - 게임 종료
- `GET /api/game/history/{userId}` - 게임 기록 조회

#### 사용자 (User)
- `GET /api/user/{id}` - 사용자 프로필 조회
- `PUT /api/user/{id}` - 사용자 프로필 수정
- `GET /api/user/{id}/stats` - 사용자 통계 조회

### 인증 방식

JWT 토큰 기반 인증을 사용합니다:
- `accessToken`: API 요청 시 사용 (짧은 만료 시간)
- `refreshToken`: 토큰 갱신 시 사용 (긴 만료 시간)

### 환경 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 설정을 추가하세요:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

## 설치 및 실행

### 1. 의존성 설치
```bash
npm install
```

### 2. 환경 변수 설정
```bash
# .env 파일 생성
cp .env.example .env
# .env 파일에서 API URL 설정
```

### 3. 개발 서버 실행
```bash
npm start
```

### 4. 빌드
```bash
npm run build
```

## 프로젝트 구조

```
src/
├── api.js                 # API 설정 및 함수들
├── App.js                 # 메인 앱 컴포넌트
├── components/            # 공통 컴포넌트
│   ├── Header.js         # 헤더 컴포넌트
│   └── Header.css
├── pages/                # 페이지 컴포넌트
│   ├── loginpage.js      # 로그인 페이지
│   ├── RegisterPage.js   # 회원가입 페이지
│   ├── KboBoard.js       # 게시판 목록
│   ├── PostForm2.js      # 게시글 작성/수정
│   ├── GamePage.js       # 게임 페이지
│   └── ...
├── styles/               # 스타일 파일들
└── utils/                # 유틸리티 함수들
```

## 주요 개선사항

### 백엔드 연동 개선
1. **중앙화된 API 관리**: `api.js`에서 모든 API 호출을 관리
2. **토큰 기반 인증**: JWT 토큰을 사용한 보안 인증
3. **자동 토큰 갱신**: 만료된 토큰 자동 갱신
4. **에러 핸들링**: 통합된 에러 처리 및 사용자 피드백
5. **로딩 상태 관리**: API 호출 시 로딩 상태 표시

### 사용자 경험 개선
1. **폼 검증**: 실시간 입력 검증 및 에러 메시지
2. **자동 저장**: 게시글 작성 시 자동 임시 저장
3. **반응형 디자인**: 다양한 화면 크기 지원
4. **접근성**: 키보드 네비게이션 및 스크린 리더 지원

## 개발 가이드

### 새로운 API 엔드포인트 추가

1. `src/api.js`에 새로운 API 함수 추가:
```javascript
export const newAPI = {
  getData: (params) => api.get('/new-endpoint', { params }),
  createData: (data) => api.post('/new-endpoint', data),
};
```

2. 컴포넌트에서 사용:
```javascript
import { newAPI } from '../api';

const response = await newAPI.getData({ param: 'value' });
```

### 에러 처리

모든 API 호출은 try-catch 블록으로 감싸고 적절한 에러 메시지를 표시하세요:

```javascript
try {
  const response = await api.getData();
  // 성공 처리
} catch (error) {
  console.error('Error:', error);
  const message = error.response?.data?.message || '오류가 발생했습니다.';
  setError(message);
}
```

## 라이선스

MIT License
