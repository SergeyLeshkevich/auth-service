package ru.clevertec.auth.service;

import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;


public interface UserViewService {

    UserResponse getUserDtoById(long id);

    UserResponse updateUser(Long id, UserRequest user);

    UserResponse create(UserRequest userRequest, String role);

    UserResponse createWithRoleSubscriber(UserRequest userRequest);

    void archive(long userId);
}
