# 홈페이지 기본베이스

현대적이고 반응형 웹사이트를 위한 React 기반 홈페이지 템플릿입니다.

## 🚀 주요 기능

- **반응형 디자인**: 모든 디바이스에서 최적화된 경험
- **모던 UI/UX**: 깔끔하고 직관적인 사용자 인터페이스
- **라우팅**: React Router를 사용한 페이지 네비게이션
- **스타일링**: Styled Components를 활용한 컴포넌트 기반 스타일링
- **접근성**: 웹 접근성 표준을 준수한 마크업

## 📁 프로젝트 구조

```
src/
├── components/          # 재사용 가능한 컴포넌트
│   ├── Header.js       # 네비게이션 헤더
│   └── Footer.js       # 푸터
├── pages/              # 페이지 컴포넌트
│   ├── HomePage.js     # 홈페이지
│   ├── AboutPage.js    # 소개 페이지
│   ├── ServicesPage.js # 서비스 페이지
│   └── ContactPage.js  # 연락처 페이지
├── App.js              # 메인 앱 컴포넌트
└── index.js            # 앱 진입점
```

## 🛠️ 기술 스택

- **React 18**: 사용자 인터페이스 구축
- **React Router**: 클라이언트 사이드 라우팅
- **Styled Components**: CSS-in-JS 스타일링
- **HTML5 & CSS3**: 시맨틱 마크업과 모던 CSS

## 🚀 시작하기

### 필수 요구사항

- Node.js (v14 이상)
- npm 또는 yarn

### 설치 및 실행

1. **의존성 설치**
   ```bash
   npm install
   ```

2. **개발 서버 실행**
   ```bash
   npm start
   ```

3. **브라우저에서 확인**
   ```
   http://localhost:3000
   ```

### 빌드

프로덕션용 빌드를 생성하려면:

```bash
npm run build
```

## 📱 페이지 구성

### 홈페이지 (/)
- 히어로 섹션
- 서비스 소개
- CTA 버튼

### 소개 페이지 (/about)
- 회사 소개
- 핵심 가치
- 성과 지표

### 서비스 페이지 (/services)
- 서비스 카탈로그
- 요금제 정보
- 기능 비교

### 연락처 페이지 (/contact)
- 연락처 폼
- 회사 정보
- 지도 (플레이스홀더)

## 🎨 커스터마이징

### 색상 테마 변경

`src/components/Header.js`와 각 페이지 컴포넌트에서 색상 값을 수정하여 테마를 변경할 수 있습니다.

### 콘텐츠 수정

각 페이지 컴포넌트의 텍스트와 이미지를 수정하여 원하는 콘텐츠로 변경하세요.

### 스타일 수정

Styled Components를 사용하여 각 컴포넌트의 스타일을 쉽게 수정할 수 있습니다.

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 🤝 기여하기

1. 이 저장소를 포크합니다
2. 새로운 기능 브랜치를 생성합니다 (`git checkout -b feature/AmazingFeature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add some AmazingFeature'`)
4. 브랜치에 푸시합니다 (`git push origin feature/AmazingFeature`)
5. Pull Request를 생성합니다

## 📞 지원

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 생성해 주세요.
