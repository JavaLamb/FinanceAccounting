package main.servletUi.dto.Request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
