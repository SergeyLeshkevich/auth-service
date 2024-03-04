package ru.clevertec.auth.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.entity.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "anUser")
public class UserTestBuilderTest implements TestBuilder<User> {

    private Long id = 1L;
    private UUID uuid = UUID.fromString("0bdc4d34-af90-4b42-bba6-f588323c87d7");
    private String userName = "Test userName";
    private String name = "Test name";
    private String password = "100";
    private String passwordConfirmation = "100";
    private Role role = Role.ROLE_ADMIN;
    private Set<Role> roles = new HashSet<>();

    @Override
    public User build() {
        roles.add(role);
        return new User(id, uuid, name, userName, password, passwordConfirmation, false, roles);
    }
}
