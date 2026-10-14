package main.controllers;

import main.dto.Request.LoginRequest;
import main.repositories.UserRepository;
import main.security.SecurityConfig;
import main.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    @Nested
    @DisplayName("POST /registration")
    class RegistrationTests{
        void shouldReturn204WhenSuccess(){}
        void shouldReturn400WhenEmailInvalid(){}
        void shouldReturn400WhenPasswordInvalid(){}
        void shouldReturn400WhenEmailAlreadyExists(){}
    }

    @Nested
    @DisplayName("POST /login")
    class AuthorizationTests {
        private static Stream<String> provideBlankString() {
            return Stream.of(
                    "   ",
                    "",
                    null
            );
        }

        @Test
        void shouldReturn204WhenSuccess() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user", "password");
            Authentication mockAuthentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNoContent());

            verify(securityContextRepository, times(1)).saveContext(any(), any(), any());
        }

        @Test
        void shouldReturn401WhenCredentialsInvalid() throws Exception {
            LoginRequest invalidRequest = new LoginRequest("WRONG_USER", "WRONG_PASSWORD");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException(anyString()));

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isUnauthorized());

            verify(securityContextRepository, never()).saveContext(any(), any(), any());
        }

        @ParameterizedTest
        @MethodSource("provideBlankString")
        void shouldReturn400WhenEmailAndPasswordIsBlank(String invalid) throws Exception {
            LoginRequest invalidRequest = new LoginRequest(invalid, invalid);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.validationErrors.password")
                            .value("Password cannot be empty"))
                    .andExpect(jsonPath("$.validationErrors.username").value(
                            "Email cannot be empty"));

            verify(authenticationManager, never()).authenticate(any());
        }
    }
}
