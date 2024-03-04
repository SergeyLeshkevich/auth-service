package ru.clevertec.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.auth.entity.user.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * This interface extends JpaRepository, providing CRUD operations and custom queries for User entities.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find.
     * @return an Optional containing the found user or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their UUID.
     *
     * @param uuid the UUID of the user to find.
     * @return an Optional containing the found user or an empty Optional if not found.
     */
    Optional<User> findByUuid(UUID uuid);
}
