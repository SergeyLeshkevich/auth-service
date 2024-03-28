package ru.clevertec.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.auth.entity.user.Role;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 * This interface extends JpaRepository, providing custom queries for Role entities.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by their name.
     *
     * @param name the name of the role to find.
     * @return an Optional containing the found role or an empty Optional if not found.
     */
    Optional<Role> findByName(String name);
}
