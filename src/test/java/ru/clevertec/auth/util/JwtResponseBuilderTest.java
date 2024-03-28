package ru.clevertec.auth.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.auth.entity.user.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aJwtResponse")
public class JwtResponseBuilderTest implements TestBuilder<JwtResponse> {

    private Long id = 1L;
    private UUID uuid = UUID.fromString("0bdc4d34-af90-4b42-bba6-f588323c87d7");
    private String userName = "Test userName";
    private Role role = Role.builder().build();
    private Set<String> roles = new HashSet<>();

    @Override
    public JwtResponse build() {
        roles.add(role.getName());
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setUuid(uuid);
        jwtResponse.setId(id);
        jwtResponse.setRoles(roles);
        jwtResponse.setUsername(userName);
        jwtResponse.setAccessToken("access-token");
        jwtResponse.setRefreshToken("valid-refresh-token");
        return jwtResponse;
    }
}
