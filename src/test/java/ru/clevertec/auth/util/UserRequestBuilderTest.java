package ru.clevertec.auth.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.auth.entity.dto.user.UserRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserRequest")
public class UserRequestBuilderTest implements TestBuilder<UserRequest> {

    private String userName = "Test userName";
    private String name = "Test name";
    private String password = "100";
    private String passwordConfirmation = "100";

    @Override
    public UserRequest build() {
        return new UserRequest(name, userName, password, passwordConfirmation);
    }
}
