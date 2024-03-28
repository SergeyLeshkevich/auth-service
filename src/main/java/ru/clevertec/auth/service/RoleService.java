package ru.clevertec.auth.service;


import ru.clevertec.auth.entity.user.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

   Role getByName(String name);
}
