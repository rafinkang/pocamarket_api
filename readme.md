# Architecture
### 헥사고날 아키텍쳐 지향 계층별로 분리

```text
src/main/java/com/venvas/pocamarket/
├── infrastructure/      # 공통 인프라
│   ├── config/         # 공통 설정
│   ├── security/       # 공통 보안
│   └── util/          # 공통 유틸리티
│
├── service/           # 각 서비스별 모듈
│   ├── pokemon/       # 포켓몬 카드 서비스
│   │   ├── api/       # API 계층
│   │   │   ├── controller/     # 컨트롤러
│   │   │   └── validator/      # API 유효성 검사
│   │   │
│   │   ├── application/ # 비즈니스 로직
│   │   │   ├── service/          # 서비스 계층
│   │   │   ├── dto/             # DTO 클래스
│   │   │   └── mapper/          # Entity ↔ DTO 매핑
│   │   │
│   │   ├── domain/    # 도메인 모델
│   │   │   ├── entity/         # 엔티티 클래스
│   │   │   ├── repository/     # 인터페이스 정의
│   │   │   ├── exception/      # 도메인 예외
│   │   │   └── value/         # 값 객체
│   │   │
│   │   └── infrastructure/ # 인프라 구현
│   │       ├── persistence/    # JPA 구현체
│   │       ├── config/         # 설정 클래스
│   │       └── security/       # 보안 관련
│   │
│   └── [new-service]/ # 새로운 서비스
│       ├── api/
│       ├── application/
│       ├── domain/
│       └── infrastructure/
│
└── common/            # 모든 서비스 공통 컴포넌트
    ├── exception/     # 공통 예외
    └── util/          # 공통 유틸리티


src/test/java/com/venvas/pocamarket/
├── service/           # 각 서비스별 테스트
│   ├── pokemon/      # 포켓몬 카드 서비스 테스트
│   │   ├── api/
│   │   │   ├── controller/
│   │   │   └── validator/
│   │   ├── application/
│   │   │   ├── service/
│   │   │   ├── dto/
│   │   │   └── mapper/
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── exception/
│   │   │   └── value/
│   │   └── infrastructure/
│   │       ├── persistence/
│   │       ├── config/
│   │       └── security/
│   └── [new-service]/ # 새로운 서비스 테스트
│       ├── api/
│       ├── application/
│       ├── domain/
│       └── infrastructure/
└── common/           # 공통 컴포넌트 테스트
    ├── exception/
    └── util/
```