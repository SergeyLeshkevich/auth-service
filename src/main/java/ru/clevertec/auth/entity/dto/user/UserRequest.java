package ru.clevertec.auth.entity.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.clevertec.auth.entity.dto.validation.OnCreate;
import ru.clevertec.auth.entity.dto.validation.OnUpdate;

public record UserRequest (

    @NotNull(message = "Name must be not null.",groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255,message = "Name length must be smaller than 255 symbols", groups = {OnCreate.class, OnUpdate.class})
    String name,

    @NotNull(message = "Username must be not null.",groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255,message = "Username length must be smaller than 255 symbols", groups = {OnCreate.class, OnUpdate.class})
   String username,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password must be not null.",groups = {OnUpdate.class, OnCreate.class})
    String password,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password must be not null.",groups =  OnCreate.class)
    String passwordConfirmation){
}
