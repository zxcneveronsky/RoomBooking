# RoomBooking — Agent Instructions

## Build & Run
- `mvn clean install` — собрать + тесты
- `mvn test` — только тесты
- `mvn spring-boot:run` — запустить приложение
- Нужен Java 21

## Architecture
- Стек: Spring Boot 4.0.1, Java 21, PostgreSQL, H2 (тесты), Flyway, JWT, Lombok
- Слойная архитектура: Controller → Service → Repository
- DTO: Java records (`dto.request`, `dto.response`)
- Маппинг: ручные `@Component` мапперы (не MapStruct)
- Кэш: Caffeine (`rooms`, `bookings`)
- Безопасность: Stateless JWT. `JwtFilter` перед `UsernamePasswordAuthenticationFilter`
- Миграции: Flyway в `src/main/resources/db/migration/`
- Lombok: `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Slf4j`

## Testing
- Конфиг: `src/test/resources/application-test.yaml` (H2 + Flyway)
- Доступны: MockMvc (`spring-boot-starter-webmvc-test`), H2
- Добавляй `@ActiveProfiles("test")` к `@SpringBootTest` тестам
- Тесты с Spring Context: `@SpringBootTest` + `@ActiveProfiles("test")`
- Тесты без Spring: `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`

## Known Issues
- `pom.xml` требует `annotationProcessorPaths` для Lombok в `maven-compiler-plugin`
