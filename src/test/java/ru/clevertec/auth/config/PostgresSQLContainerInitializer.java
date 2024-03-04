package ru.clevertec.auth.config;


import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@Transactional
@ActiveProfiles("test")
public abstract class PostgresSQLContainerInitializer {

    private static final PostgreSQLContainer<?> postgreSQLContainer=
            new PostgreSQLContainer<>("postgres:13.3");

    @BeforeAll
    static void startContainer(){
        postgreSQLContainer
                .start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",postgreSQLContainer::getJdbcUrl);
    }
}
