package ru.clevertec.auth.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.auth.config.PostgresSQLContainerInitializer;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.util.UserRequestBuilderTest;
import ru.clevertec.auth.util.UserResponseBuilderTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest extends PostgresSQLContainerInitializer {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldCreateUser() throws Exception {
        UserRequest newUser = UserRequestBuilderTest.aUserRequest().withUserName("NewUser").build();
        UserResponse userResponse = UserResponseBuilderTest.aUserResponse().build();
        JsonMapper jsonMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        String json = jsonMapper.writeValueAsString(newUser);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .param("role","ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse actual = objectMapper.readValue(response, UserResponse.class);

        assertThat(actual.id()).isNotNull();
        assertThat(actual.name()).isEqualTo(userResponse.name());
        assertThat(actual.username()).isEqualTo("NewUser");
    }


    @Test
    void shouldUpdateUser() throws Exception {
        UserRequest updatedUser = UserRequestBuilderTest.aUserRequest().withUserName("Update").build();
        JsonMapper jsonMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        String json = jsonMapper.writeValueAsString(updatedUser);
        UserResponse userResponse = UserResponseBuilderTest.aUserResponse().build();
        String response = mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserResponse actual = jsonMapper.readValue(response, UserResponse.class);

        assertThat(actual.id()).isEqualTo(userResponse.id());
        assertThat(actual.name()).isEqualTo(userResponse.name());
        assertThat(actual.username()).isEqualTo("Update");
    }


    @Test
    void shouldGetUserById() throws Exception {
        long userId = 1L;
        UserResponse userResponse = UserResponseBuilderTest.aUserResponse().build();
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse actual = objectMapper.readValue(response, UserResponse.class);

        assertThat(actual.id()).isEqualTo(userResponse.id());
        assertThat(actual.name()).isEqualTo(userResponse.name());
        assertThat(actual.username()).isEqualTo(userResponse.username());
    }


    @Test
    void shouldArchiveUserById() throws Exception {
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + userId))
                .andExpect(status().isOk());
    }
}
