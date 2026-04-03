# Spring AI Profile Chat

포트폴리오 방문자가 개발자에 대해 자연어로 질문할 수 있는 AI 챗봇 서버입니다.
RAG(Retrieval-Augmented Generation) 기반으로 개발자 프로필 데이터를 벡터 검색하여 맥락 있는 답변을 생성합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.4 |
| AI | Spring AI 1.0.0-M6, OpenAI (GPT-3.5-turbo, text-embedding-3-small) |
| Vector DB | Qdrant |
| Docs | SpringDoc OpenAPI (Swagger) |
| Build | Gradle |

---

## 동작 방식

### 초기화 (앱 실행 시)
```
profile-context.md 로드
    → TokenTextSplitter (512 tokens 단위 청크)
    → OpenAI text-embedding-3-small (벡터화)
    → Qdrant Vector Store 저장
```

### 질문 처리
```
사용자 질문
    → 질문 벡터화
    → Qdrant 유사도 검색 (상위 3개 청크 추출)
    → 시스템 프롬프트 구성 (컨텍스트 포함)
    → OpenAI GPT-3.5-turbo 호출
    → 답변 반환
```

---

## API 엔드포인트

### POST `/api/v1/profile/chat`
프로필 기반 RAG 챗봇 (메인 기능)

**Request**
```json
{
  "question": "어떤 프로젝트를 진행했나요?"
}
```

**Response**
```json
{
  "answer": "FLOFI, Movie Web, Workfolio 세 가지 프로젝트를 진행했습니다. ..."
}
```

### POST `/api/v1/chat/query`
일반 OpenAI 챗 (컨텍스트 없음)

**Request**
```json
{
  "query": "Spring Boot란?",
  "model": "gpt-3.5-turbo"
}
```

### GET `/api-docs`
Swagger UI API 문서

---

## 프로젝트 구조

```
src/main/
├── java/com/example/spring_ai_tutorial/
│   ├── config/
│   │   ├── OpenAiConfig.java              # OpenAI API 설정
│   │   ├── OpenApiConfig.java             # Swagger 설정
│   │   └── WebConfig.java                 # CORS 설정
│   ├── controller/
│   │   ├── ProfileChatController.java     # 프로필 RAG 챗 엔드포인트
│   │   └── ChatController.java            # 일반 챗 엔드포인트
│   ├── service/
│   │   ├── RagService.java                # RAG 워크플로우
│   │   ├── ChatService.java               # OpenAI API 호출
│   │   ├── DocumentProcessingService.java # PDF 텍스트 추출
│   │   └── ProfileContextInitializer.java # 앱 시작 시 프로필 로드
│   ├── repository/
│   │   └── InMemoryDocumentVectorStore.java
│   └── domain/dto/
│       ├── ProfileChatRequestDto.java
│       ├── ProfileChatResponseDto.java
│       └── ApiResponseDto.java
└── resources/
    ├── application.properties
    ├── profile/
    │   └── profile-context.md             # RAG 데이터 소스 (개발자 프로필)
    └── static/
        ├── index.html                     # 챗봇 메인 UI
        └── chat-widget.html               # 포트폴리오 삽입용 위젯
```

---

## 실행 방법

### 사전 요구사항
- Java 17
- Qdrant 실행 중 (기본 `localhost:6334`)
- OpenAI API Key

### 환경변수 설정
```bash
export OPENAI_API_KEY=sk-...
```

### Qdrant 실행 (Docker)
```bash
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
```

### 빌드 및 실행
```bash
./gradlew bootRun
```

서버 실행 후 `http://localhost:8080` 에서 챗봇 UI 확인 가능

---

## CORS 허용 출처

| 출처 | 용도 |
|------|------|
| `https://uriml.github.io` | 포트폴리오 사이트 |
| `http://localhost:5500` | 로컬 개발 |
| `http://127.0.0.1:5500` | 로컬 개발 |
