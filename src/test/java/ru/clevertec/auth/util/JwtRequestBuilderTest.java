package ru.clevertec.auth.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.auth.entity.dto.auth.JwtRequest;


@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aJwtRequest")
public class JwtRequestBuilderTest implements TestBuilder<JwtRequest> {

    private String userName = "Test userName";
    private String password = "100";

    @Override
    public JwtRequest build() {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setPassword("100");
        jwtRequest.setUsername(userName);
        return jwtRequest;
    }
}
