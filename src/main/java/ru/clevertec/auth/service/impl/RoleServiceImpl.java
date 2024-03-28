package ru.clevertec.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.repository.RoleRepository;
import ru.clevertec.auth.service.RoleService;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                ()-> EntityNotFoundException.of(Role.class,name));
    }
}
