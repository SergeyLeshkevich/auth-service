package ru.clevertec.auth.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.auth.entity.user.Role;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aRole")
public class RoleTestBuilder implements TestBuilder<Role> {

    private Long id = 1L;
    private String name = "ROLE_ADMIN";

    @Override
    public Role build() {
        return new Role(id, name);
    }
}
