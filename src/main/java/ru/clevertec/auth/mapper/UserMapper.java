package ru.clevertec.auth.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.entity.user.User;


/**
 * Mapper interface for converting between User entity and UserResponse DTO.
 * This interface is used to abstract the conversion logic between the database entity and the data transfer object.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity to convert.
     * @return the converted UserResponse DTO.
     */
    UserResponse toDto(User user);
}
