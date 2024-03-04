package ru.clevertec.auth.service;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.clevertec.auth.mapper.UserMapper;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.repository.UserRepository;
import ru.clevertec.auth.service.impl.UserServiceImpl;
import ru.clevertec.auth.util.UserRequestBuilderTest;
import ru.clevertec.auth.util.UserResponseBuilderTest;
import ru.clevertec.auth.util.UserTestBuilderTest;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;
import ru.clevertec.exceptionhandlerstarter.exception.UniqueUsernameException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void shouldReturnUserWhenIdExists() {
        // given
        long id = 1L;
        User expectedUser = UserTestBuilderTest.anUser().build();
        expectedUser.setId(id);
        given(userRepository.findById(id)).willReturn(Optional.of(expectedUser));

        // when
        User actualUser = userService.getById(id);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }


    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundById() {
        // given
        long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.getById(id));

        // then
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with 1 not found");
    }


    @Test
    void shouldReturnUserWhenUsernameExists() {
        // given
        User expectedUser = UserTestBuilderTest.anUser().build();
        String username = expectedUser.getUsername();
        given(userRepository.findByUsername(expectedUser.getUsername())).willReturn(Optional.of(expectedUser));

        // when
        User actualUser = userService.getByUsername(username);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }


    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUsernameNotFound() {
        // given
        String username = "nonexistentUser";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.getByUsername(username));

        // then
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }


    @Test
    void shouldReturnUserDtoWhenIdExists() {
        // given
        long id = 1L;
        User user = UserTestBuilderTest.anUser().build();
        user.setId(id);
        UserResponse expectedDto = UserResponseBuilderTest.aUserResponse().build();
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserResponse actualDto = userService.getUserDtoById(id);

        // then
        assertThat(actualDto).isEqualTo(expectedDto);
    }


    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundByIdForDto() {
        // given
        long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.getUserDtoById(id));

        // then
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with 1 not found");
    }

    // Тест на успешное обновление пользователя
    @Test
    void shouldUpdateUserSuccessfully() {
        // given
        Long id = 1L;
        User existingUser = UserTestBuilderTest.anUser().build();
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        UserResponse expectedResponse = UserResponseBuilderTest.aUserResponse().build();
        given(userRepository.findById(id)).willReturn(Optional.of(existingUser));
        given(userRepository.findByUsername(existingUser.getUsername())).willReturn(Optional.empty());
        given(passwordEncoder.encode("100")).willReturn("100");
        given(userRepository.save(existingUser)).willReturn(existingUser);
        given(userMapper.toDto(existingUser)).willReturn(expectedResponse);

        // when
        UserResponse actualResponse = userService.updateUser(id, userRequest);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo("Test name");
        assertThat(savedUser.getUsername()).isEqualTo("Test userName");
        assertThat(savedUser.getPassword()).isEqualTo("100");
    }

    @Test
    void shouldThrowUniqueUsernameExceptionWhenUsernameExists() {
        // given
        Long id = 1L;
        User existingUser = UserTestBuilderTest.anUser().withUserName("otherName").build();
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        given(userRepository.findById(id)).willReturn(Optional.of(existingUser));
        given(userRepository.findByUsername(userRequest.username())).willReturn(Optional.of(existingUser));

        // when
        Throwable thrown = catchThrowable(() -> userService.updateUser(id, userRequest));

        // then
        assertThat(thrown).isInstanceOf(UniqueUsernameException.class)
                .hasMessageContaining("User with the current username already exists");
    }


    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundByIdForUpdate() {
        // given
        Long id = 1L;
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.updateUser(id, userRequest));

        // then
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with 1 not found");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // given
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        Role role = Role.ROLE_ADMIN;
        User user = UserTestBuilderTest.anUser().build();
        UserResponse expectedResponse = UserResponseBuilderTest.aUserResponse().build();
        given(userRepository.findByUsername(userRequest.username())).willReturn(Optional.empty());
        given(passwordEncoder.encode(userRequest.passwordConfirmation())).willReturn("100");
        given(userMapper.toDto(user)).willReturn(expectedResponse);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse actualResponse = userService.create(userRequest, role);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo(userRequest.name());
        assertThat(savedUser.getUsername()).isEqualTo(userRequest.username());
        assertThat(savedUser.getPassword()).isEqualTo("100");
        assertThat(savedUser.getRoles()).contains(role);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPasswordsDoNotMatch() {
        // given
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().withPasswordConfirmation("200").build();
        Role role = Role.ROLE_ADMIN;

        // when
        Throwable thrown = catchThrowable(() -> userService.create(userRequest, role));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password and password confirmation do not match.");
    }

    @Test
    void shouldCreateUserWithRoleSubscriberSuccessfully() {
        // given
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        User user = UserTestBuilderTest.anUser().build();
        UserResponse expectedResponse = UserResponseBuilderTest.aUserResponse().build();
        given(userRepository.findByUsername("Test name")).willReturn(Optional.empty());
        given(passwordEncoder.encode(userRequest.passwordConfirmation())).willReturn("encodedPassword");
        given(userMapper.toDto(user)).willReturn(expectedResponse);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse actualResponse = userService.createWithRoleSubscriber(userRequest);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo(userRequest.name());
        assertThat(savedUser.getUsername()).isEqualTo(userRequest.username());
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRoles()).containsOnly(Role.ROLE_SUBSCRIBER);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPasswordsDoNotMatchForSubscriber() {
        // given
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().withPasswordConfirmation("200").build();

        // when
        Throwable thrown = catchThrowable(() -> userService.createWithRoleSubscriber(userRequest));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password and password confirmation do not match.");
    }

    @Test
    void shouldArchiveUserSuccessfully() {
        // given
        long userId = 1L;
        User existingUser = UserTestBuilderTest.anUser().build();
        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        // when
        userService.archive(userId);

        // then
        assertThat(existingUser.isArchived()).isTrue();
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundForArchive() {
        // given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.archive(userId));

        // then
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with 1 not found");
    }

    @Test
    void shouldReturnTrueWhenUuidExists() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        UUID uuid = UUID.randomUUID();
        User user = UserTestBuilderTest.anUser().build();
        user.setUuid(uuid);
        given(userRepository.findByUuid(uuid)).willReturn(Optional.of(user));

        // when
        Method isExistMethod = UserServiceImpl.class.getDeclaredMethod("isExist", UUID.class);
        isExistMethod.setAccessible(true);
        boolean exists = (boolean) isExistMethod.invoke(userService, uuid);

        // then
        assertThat(exists).isTrue();
    }


    @Test
    void shouldReturnFalseWhenUuidDoesNotExist() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // when
        Method isExistMethod = UserServiceImpl.class.getDeclaredMethod("isExist", UUID.class);
        isExistMethod.setAccessible(true);
        boolean exists = (boolean) isExistMethod.invoke(userService, uuid);

        // then
        assertThat(exists).isFalse();
    }
}


