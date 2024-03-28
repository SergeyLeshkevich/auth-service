package ru.clevertec.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.service.UserInnerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for JWT user details.
 * This class is responsible for loading user-specific data.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserInnerService userService;

    /**
     * Loads the user by username.
     *
     * @param username the username to search for in the user service.
     * @return UserDetails object containing user information.
     * @throws UsernameNotFoundException if the username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        return create(user);
    }

    /**
     * Creates a JwtEntity based on the user data.
     *
     * @param user the user object to create a JwtEntity from.
     * @return JwtEntity containing user's ID, UUID, username, name, password, and authorities.
     */
    private JwtEntity create(User user) {
        Set<Role> roles = user.getRoles();
        return JwtEntity.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .name(user.getName())
                .password(user.getPassword())
                .authorities(mapToGrantedAuthorities(new ArrayList<>(user.getRoles())))
                .build();
    }

    /**
     * Maps a list of roles to a list of GrantedAuthority objects.
     *
     * @param roles the roles to map to GrantedAuthority objects.
     * @return a list of GrantedAuthority objects.
     */
    private List<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
