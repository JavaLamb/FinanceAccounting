package main.controllers;

import main.converters.UserToUserResponseConverter;
import main.dto.Request.LoginRequest;
import main.dto.Request.RegiRequest;
import main.dto.Response.UserResponse;
import main.entities.User;
import main.exceptions.ValidationException;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
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
    private ObjectMapper om;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserToUserResponseConverter converter;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    @Nested
    @DisplayName("POST /registration")
    class RegistrationTests {

        private static Stream<String> provideBlankString() {
            return Stream.of(
                    "",
                    null
            );
        }

        private static Stream<String> provideInvalidEmail() {
            return Stream.of(
                    "textWith@",
                    "justText",
                    "@without.com"
            );
        }

        private static Stream<String> provideInvalidPassword() {
            return Stream.of(
                    "password1.",
                    "passworD1",
                    "PASSWORD1."
            );
        }

        @Test
        void shouldReturn204WhenSuccess() throws Exception {
            RegiRequest request = new RegiRequest("email@gmail.com", "passworD1.");
            UserResponse response = new UserResponse(1L, "email@gmail.com");
            User user = mock(User.class);
            when(userService.registration(request))
                    .thenReturn(user);
            when(converter.convert(user))
                    .thenReturn(response);

            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("email@gmail.com"));

            verify(userService).registration(request);
            verify(converter).convert(user);
        }

        @ParameterizedTest
        @MethodSource("provideBlankString")
        void shouldReturn400WhenEmailIsBlank(String invalid) throws Exception {
            RegiRequest invalidRequest = new RegiRequest(invalid, "passworD1.");

            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.validationErrors.username")
                            .value("Email cannot be empty"));

            verify(userService, never()).registration(any());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidEmail")
        void shouldReturn400WhenEmailInvalid(String invalid) throws Exception {
            RegiRequest invalidRequest = new RegiRequest(invalid, "passworD1.");

            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.validationErrors.username")
                            .value("Incorrect format of email"));

            verify(userService, never()).registration(any());
        }

        @Test
        void shouldReturn400WhenPasswordIsEmpty() throws Exception {
            RegiRequest invalidRequest = new RegiRequest("user@gmail.com", "");

            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.validationErrors.password", containsInAnyOrder(
                            "Password cannot be empty",
                            "Length must be from 8 to 32 symbols",
                            "The password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character."
                    )));

            verify(userService, never()).registration(any());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidPassword")
        void shouldReturn400WhenPasswordInvalid(String invalid) throws Exception {
            RegiRequest invalidRequest = new RegiRequest("username@mail.com", invalid);

            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.validationErrors.password")
                            .value("The password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character."));

            verify(userService, never()).registration(any());
        }

        @Test
        void shouldReturn400WhenEmailAlreadyExists() throws Exception {
            when(userService.registration(any()))
                    .thenThrow(new ValidationException(Map.of("username", List.of("Email is already exists"))));
            RegiRequest request = new RegiRequest("username@gmail.com", "passworD1.");
            mockMvc.perform(post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.username").value("Email is already exists"));

            verify(converter, never()).convert(any());
        }
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
                            .content(om.writeValueAsString(loginRequest)))
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
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isUnauthorized());

            verify(securityContextRepository, never()).saveContext(any(), any(), any());
        }

        @ParameterizedTest
        @MethodSource("provideBlankString")
        void shouldReturn400WhenEmailAndPasswordIsBlank(String invalid) throws Exception {
            LoginRequest invalidRequest = new LoginRequest(invalid, invalid);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
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
