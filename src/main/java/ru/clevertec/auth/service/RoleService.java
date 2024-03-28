package ru.clevertec.auth.service;


import ru.clevertec.auth.entity.user.Role;

public interface RoleService {

   Role getByName(String name);
}
