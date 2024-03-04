package ru.clevertec.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.repository.UserRepository;
import ru.clevertec.auth.service.UserInnerService;
import ru.clevertec.auth.service.UserViewService;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.mapper.UserMapper;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;
import ru.clevertec.exceptionhandlerstarter.exception.UniqueUsernameException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of user services, handling core user operations such as retrieval, creation,
 * and updating of user records.
 * This class interacts with the UserRepository to perform database operations.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserInnerService, UserViewService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the found User object.
     * @throws EntityNotFoundException if no user is found with the provided ID.
     */
    @Override
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.of(User.class, id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve.
     * @return the found User object.
     * @throws UsernameNotFoundException if no user is found with the provided username.
     */
    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.of(User.class,username) );
    }

    /**
     * Retrieves a DTO representation of a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the UserResponse DTO of the found user.
     * @throws EntityNotFoundException if no user is found with the provided ID.
     */
    @Override
    public UserResponse getUserDtoById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.of(User.class, id));
        return userMapper.toDto(user);
    }


    /**
     * Updates a user's information based on the provided UserRequest object.
     *
     * @param id   the ID of the user to update.
     * @param user the UserRequest object containing the new user information.
     * @return the updated UserResponse DTO.
     * @throws UniqueUsernameException if the username is already taken by another user.
     */
    @Override
    public UserResponse updateUser(Long id, UserRequest user) {
        User existing = getById(id);
        Optional<User> byUsername = userRepository.findByUsername(user.username());
        if (byUsername.isPresent()
                && !existing.getUsername().equals(user.username())) {
            throw new UniqueUsernameException("User with the current username already exists");
        }
        existing.setName(user.name());
        existing.setUsername(user.username());
        existing.setPassword(passwordEncoder.encode(user.password()));
        return userMapper.toDto(userRepository.save(existing));
    }

    /**
     * Creates a new user with the specified role.
     *
     * @param userRequest the UserRequest object containing the user's information.
     * @param role        the role to assign to the new user.
     * @return the created UserResponse DTO.
     */
    @Override
    @Transactional
    public UserResponse create(UserRequest userRequest, Role role) {
        validate(userRequest.username(), userRequest);

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (isExist(uuid));

        User user = User.builder()
                .name(userRequest.name())
                .uuid(uuid)
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.passwordConfirmation()))
                .roles(Set.of(role,Role.ROLE_SUBSCRIBER))
                .build();

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Creates a new user with the role of a subscriber.
     *
     * @param userRequest the UserRequest object containing the user's information.
     * @return the created UserResponse DTO.
     */
    @Override
    public UserResponse createWithRoleSubscriber(UserRequest userRequest) {
        validate(userRequest.name(), userRequest);

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (isExist(uuid));

        User user = User.builder()
                .name(userRequest.name())
                .uuid(uuid)
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.passwordConfirmation()))
                .roles(Set.of(Role.ROLE_SUBSCRIBER))
                .build();

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Archives a user by their ID, effectively soft-deleting the user record.
     *
     * @param userId the ID of the user to archive.
     */
    @Override
    public void archive(long userId) {
        User existing = getById(userId);
        existing.setArchived(true);
        userRepository.save(existing);
    }

    /**
     * Checks if a house with the specified UUID exists.
     *
     * @param uuid The UUID to check.
     * @return True if the house exists, false otherwise.
     */
    private boolean isExist(UUID uuid) {
        return userRepository.findByUuid(uuid).isPresent();
    }

    /**
     * Validates the uniqueness of the username and the matching of passwords in the UserRequest object.
     *
     * @param username    the username to check for uniqueness.
     * @param userRequest the UserRequest object containing the user's information.
     * @throws IllegalArgumentException if the username is already taken or the passwords do not match.
     */
    private void validate(String username, UserRequest userRequest) {

        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            throw new IllegalArgumentException("User already exists.");
        }
        if (!userRequest.password().equals(userRequest.passwordConfirmation())) {
            throw new IllegalArgumentException(
                    "Password and password confirmation do not match."
            );
        }
    }
}
