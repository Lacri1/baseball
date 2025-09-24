# 백엔드 CORS 설정 가이드

## 문제 상황
프론트엔드에서 백엔드 API로 요청 시 다음과 같은 CORS 에러가 발생합니다:
```
OPTIONS "/api/game", parameters={}
WARN org.springframework.web.servlet.PageNotFound - No mapping for OPTIONS /api/game
```

## 해결 방법

### 1. Spring Boot CORS 설정

#### 방법 1: @CrossOrigin 어노테이션 사용
```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, 
             allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class GameController {
    
    @PostMapping("/game")
    public ResponseEntity<?> createGame(@RequestBody GameRequest request) {
        // 게임 생성 로직
        return ResponseEntity.ok(gameService.createGame(request));
    }
}
```

#### 방법 2: WebMvcConfigurer 구현 (권장)
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

#### 방법 3: CorsConfiguration Bean 사용
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 2. Security 설정 (Spring Security 사용 시)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/health").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 3. 필수 API 엔드포인트

프론트엔드에서 사용하는 주요 API 엔드포인트들:

#### 헬스 체크 (Health Check) - 필수
```
GET /api/health
```
**응답 예시:**
```json
{
  "success": true,
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00Z"
  },
  "message": "서버가 정상적으로 동작 중입니다."
}
```

#### 인증 (Authentication)
```
POST /api/auth/login
POST /api/auth/register  
POST /api/auth/logout
POST /api/auth/refresh
```

#### 게시판 (Board)
```
GET    /api/board?page=0&size=10&category=kbo&keyword=검색어
GET    /api/board/{id}
POST   /api/board
PUT    /api/board/{id}
DELETE /api/board/{id}
```

#### 게임 (Game)
```
POST /api/game
GET    /api/game/{id}
PUT    /api/game/{id}
POST   /api/game/{id}/end
GET    /api/game/history/{userId}
```

#### 사용자 (User)
```
GET /api/user/{id}
PUT /api/user/{id}
GET /api/user/{id}/stats
```

#### 팀 (Team)
```
GET /api/team/list
GET /api/team/{id}
```

### 4. 응답 형식

모든 API는 다음 형식으로 응답해야 합니다:

#### 성공 응답
```json
{
  "success": true,
  "data": {
    // 실제 데이터
  },
  "message": "성공 메시지"
}
```

#### 에러 응답
```json
{
  "success": false,
  "message": "에러 메시지",
  "error": "에러 코드 (선택사항)"
}
```

### 5. 헬스 체크 컨트롤러 예시

```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("status", "UP");
        data.put("timestamp", new Date());
        
        response.put("success", true);
        response.put("data", data);
        response.put("message", "서버가 정상적으로 동작 중입니다.");
        
        return ResponseEntity.ok(response);
    }
}
```

### 6. 테스트 방법

1. 백엔드 서버 실행 (포트 8080)
2. 프론트엔드 개발 서버 실행 (포트 3000)
3. 브라우저 개발자 도구에서 Network 탭 확인
4. OPTIONS 요청이 200으로 응답하는지 확인
5. `/api/health` 엔드포인트 테스트

### 7. 추가 설정

#### application.properties/yml
```properties
# CORS 관련 로그 활성화
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG

# 서버 포트 설정
server.port=8080

# CORS 설정 (선택사항)
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:3001
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

### 8. 문제 해결

#### OPTIONS 요청이 404인 경우
- CORS 설정이 제대로 적용되지 않음
- Security 설정에서 CORS가 비활성화됨
- 컨트롤러에 OPTIONS 메서드 매핑이 없음

#### 해결 방법
1. CORS 설정 확인
2. Security 설정에서 CORS 활성화
3. 모든 컨트롤러에 @CrossOrigin 추가 또는 전역 설정 사용
4. `/api/health` 엔드포인트 추가하여 연결 상태 확인

#### 디버깅 팁
1. 브라우저 개발자 도구에서 Network 탭 확인
2. 백엔드 로그에서 CORS 관련 메시지 확인
3. `curl -X OPTIONS http://localhost:8080/api/health` 명령어로 테스트

### 9. 프로덕션 환경

프로덕션에서는 실제 도메인으로 CORS 설정을 변경해야 합니다:

```java
.allowedOrigins("https://yourdomain.com", "https://www.yourdomain.com")
```

### 10. 프록시 설정 (개발 환경)

프론트엔드에서 이미 프록시 설정이 되어 있으므로, 백엔드에서 CORS 설정이 완료되면 자동으로 연동됩니다.

## 참고 자료

- [Spring Boot CORS Documentation](https://spring.io/guides/gs/rest-service-cors/)
- [Spring Security CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [Spring Boot Actuator Health Check](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health)
