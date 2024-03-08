package ru.clevertec.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.service.UserViewService;


@Validated
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController{

    private final UserViewService userService;


    @Override
    public UserResponse update(Long id, UserRequest dto) {
        return userService.updateUser(id, dto);
    }

    @Override
    public UserResponse create(UserRequest dto,Role role) {
        return userService.create(dto,role);
    }


    @Override
    public UserResponse getById(Long id) {
        return userService.getUserDtoById(id);
    }


    @Override
    public void archivedById(Long id) {
        userService.archive(id);
    }
}
