package ru.clevertec.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.repository.RoleRepository;
import ru.clevertec.auth.service.RoleService;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;

/**
 * Implementation of role services.
 * This class interacts with the RoleRepository to perform database operations.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Retrieves a role by their name.
     *
     * @param name the name of the role to retrieve.
     * @return the found Role object.
     * @throws EntityNotFoundException if no role is found with the provided name.
     */
    @Override
    public Role getByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                ()-> EntityNotFoundException.of(Role.class,name));
    }
}
