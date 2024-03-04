package ru.clevertec.auth.service;


import ru.clevertec.auth.entity.user.User;

public interface UserInnerService {

    User getById(long id);

    User getByUsername(String username);
}
