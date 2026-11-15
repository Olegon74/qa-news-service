package com.example.qanewsservice.integration.kafka.base;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.qanewsservice.integration.kafka.support.KafkaTestConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.lifecycle.Startables;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka-it")
public abstract class AbstractKafkaIntegrationTest {

    protected static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16"))
                    .withDatabaseName("qa_news_it")
                    .withUsername("qa_news_it")
                    .withPassword("qa_news_it");
    protected static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));

    static {
        Startables.deepStart(POSTGRES, KAFKA).join();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            KAFKA.stop();
            POSTGRES.stop();
        }, "kafka-it-container-cleanup"));
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${app.kafka.topics.published}")
    protected String publishedTopic;

    @Value("${app.kafka.topics.archived}")
    protected String archivedTopic;

    @Value("${test.kafka.assignment-timeout}")
    protected Duration assignmentTimeout;

    @Value("${test.kafka.positive-timeout}")
    protected Duration positiveTimeout;

    @Value("${test.kafka.negative-observation-window}")
    protected Duration negativeWindow;

    @Value("${test.kafka.poll-interval}")
    protected Duration pollInterval;

    protected String adminToken;

    @BeforeEach
    void loginAdmin() {
        adminToken = login("admin", "admin123");
    }

    protected String login(String username, String password) {
        String token = given().port(port).contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .post("/api/auth/login")
                .then().statusCode(200).extract().path("token");
        assertThat(token).isNotBlank();
        return token;
    }

    protected JsonNode createDraft() {
        String title = "Kafka integration " + UUID.randomUUID();
        Response response = given().port(port).auth().oauth2(adminToken).contentType(ContentType.JSON)
                .body(Map.of("title", title, "previewText", "Integration preview",
                        "fullText", "Integration test content", "author", "Kafka IT"))
                .post("/api/news");
        response.then().statusCode(201);
        return parse(response);
    }

    protected Response transition(long newsId, String action, String token) {
        var request = given().port(port).accept(ContentType.JSON);
        if (token != null) {
            request.auth().oauth2(token);
        }
        return request.post("/api/news/{id}/{action}", newsId, action);
    }

    protected JsonNode getNews(long newsId) {
        Response response = given().port(port).auth().oauth2(adminToken)
                .get("/api/news/{id}", newsId);
        response.then().statusCode(200);
        return parse(response);
    }

    protected JsonNode parse(Response response) {
        try {
            return objectMapper.readTree(response.asString());
        } catch (Exception failure) {
            throw new AssertionError("Invalid API JSON response", failure);
        }
    }

    protected KafkaTestConsumer consumer(String topic) {
        return new KafkaTestConsumer(KAFKA.getBootstrapServers(), topic, objectMapper,
                assignmentTimeout, pollInterval);
    }
}
