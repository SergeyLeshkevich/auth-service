package ru.clevertec.auth.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.auth.config.PostgresSQLContainerInitializer;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.util.JwtResponseBuilderTest;
import ru.clevertec.auth.util.UserRequestBuilderTest;
import ru.clevertec.auth.util.UserResponseBuilderTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest extends PostgresSQLContainerInitializer {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldLoginUser() throws Exception {
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        JsonMapper jsonMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(jsonMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponse jwtResponse = jsonMapper.readValue(response, JwtResponse.class);
        assertThat(jwtResponse.getAccessToken()).isNotBlank();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().withUserName("NewUser").build();
        JsonMapper jsonMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        UserResponse userResponse = UserResponseBuilderTest.aUserResponse().build();
        String json = jsonMapper.writeValueAsString(userRequest);

        String response = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse actual = jsonMapper.readValue(response, UserResponse.class);
        assertThat(actual.id()).isEqualTo(2L);
        assertThat(actual.name()).isEqualTo(userResponse.name());
        assertThat(actual.username()).isEqualTo("NewUser");
    }

    @Test
    void shouldRefreshToken() throws Exception {
        String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IHVzZXJOYW1lIiwiaWQiOjEsImV4cC" +
                "I6MjIzOTUwNTA5NDE1MTQyfQ.09CZD97fSbHnashAD4B0AKeaY4kDE1pUToG-yOriMIvye9pRtdIOD6" +
                "XzaZnbrVHwCnhlnjERlEcfXc6VN7xY6A";

        JwtResponse jwtResponse = JwtResponseBuilderTest.aJwtResponse().build();
        JsonMapper jsonMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

        String response = mockMvc.perform(post("/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(refreshToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponse actual = jsonMapper.readValue(response, JwtResponse.class);
        assertThat(actual.getId()).isEqualTo(jwtResponse.getId());
        assertThat(actual.getAccessToken()).isNotBlank();
        assertThat(actual.getRefreshToken()).isNotBlank();
        assertThat(actual.getUsername()).isEqualTo(jwtResponse.getUsername());
        assertThat(actual.getRoles()).isEqualTo(jwtResponse.getRoles());
        assertThat(actual.getUuid()).isEqualTo(jwtResponse.getUuid());
    }

    @Test
    void shouldValidateToken() throws Exception {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IHVzZXJOYW1lIiwiaW" +
                "QiOjEsImV4cCI6MjIzOTUwNTA5NDE1MTQyfQ.09CZD97fSbHnashAD4B0AKeaY4" +
                "kDE1pUToG-yOriMIvye9pRtdIOD6XzaZnbrVHwCnhlnjERlEcfXc6VN7xY6A";

        mockMvc.perform(post("/auth/validate")
                        .contentType(APPLICATION_JSON)
                        .content(token))
                .andExpect(status().isOk());
    }
}
