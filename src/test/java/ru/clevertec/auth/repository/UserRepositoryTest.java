package ru.clevertec.auth.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.clevertec.auth.config.PostgresSQLContainerInitializer;
import ru.clevertec.auth.entity.user.User;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest extends PostgresSQLContainerInitializer {

    private final TestEntityManager testEntityManager;
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(TestEntityManager testEntityManager, UserRepository userRepository) {
        this.testEntityManager = testEntityManager;
        this.userRepository = userRepository;
    }


    @Test
    void shouldReturnedUserByUuid() {
        //given
        User user = testEntityManager.find(User.class, 1);
        UUID uuid = UUID.fromString("0bdc4d34-af90-4b42-bba6-f588323c87d7");

        //when
        Optional<User> actual = userRepository.findByUuid(uuid);

        //then
        assertThat(actual).isEqualTo(Optional.of(user));
    }

    @Test
    void shouldReturnedUserByUsername() {
        //given
        User user = testEntityManager.find(User.class, 1);
        String username = "Test userName";

        //when
        Optional<User> actual = userRepository.findByUsername(username);

        //then
        assertThat(actual).isEqualTo(Optional.of(user));
    }
}
