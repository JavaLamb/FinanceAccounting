package main.service;

import main.dto.Request.RegiRequest;
import main.entities.User;
import main.exceptions.ValidationException;
import main.repositories.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    UserService subj;

    @Test
    void shouldThrowExceptionWhenUserAlreadyExist() {
        RegiRequest request = new RegiRequest("email", "password");
        Mockito.when(userRepository.existsByEmail("email")).thenReturn(true);

        assertThatThrownBy(() -> subj.registration(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Validation failed")
                .hasFieldOrPropertyWithValue("errorCode", "VALIDATION_ERROR")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .extracting("errors")
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsKey("username");

        verify(userRepository, never()).save(any());
        verify(encoder, never()).encode(any());
    }

    @Test
    void shouldSuccessfullyRegisterIfUserNotYetExist() {
        RegiRequest request = new RegiRequest("email", "password");
        Mockito.when(userRepository.existsByEmail("email")).thenReturn(false);
        Mockito.when(encoder.encode("password")).thenReturn("hashedPassword");
        User mockSavedUser = new User("email", "hashedPassword");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(mockSavedUser);

        User res = subj.registration(request);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User userToSave = captor.getValue();

        assertThat(res).isEqualTo(mockSavedUser);

        assertThat(userToSave.getEmail()).isEqualTo("email");
        assertThat(userToSave.getHashPassword()).isEqualTo("hashedPassword");
    }
}