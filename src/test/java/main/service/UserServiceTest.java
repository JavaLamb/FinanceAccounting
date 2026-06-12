package main.service;

import main.dao.UserDao;
import main.entities.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void findByEmailService_ReturnsUser() {
        String email = "test@mail.com";

        User user = new User(
                1,
                email,
                BCrypt.hashpw("123456", BCrypt.gensalt())
        );

        when(userDao.findByEmail(email)).thenReturn(user);

        User result = userService.findByEmailService(email);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(email, result.getEmail());

        verify(userDao).findByEmail(email);
    }

    @Test
    void checkPassword_ReturnsTrue_WhenPasswordCorrect() {
        String password = "123456";

        User user = new User(
                1,
                "test@mail.com",
                BCrypt.hashpw(password, BCrypt.gensalt())
        );

        assertTrue(userService.checkPassword(password, user));
    }

    @Test
    void checkPassword_ReturnsFalse_WhenPasswordIncorrect() {
        User user = new User(
                1,
                "test@mail.com",
                BCrypt.hashpw("correctPassword", BCrypt.gensalt())
        );

        assertFalse(
                userService.checkPassword("wrongPassword", user)
        );
    }

    @Test
    void createUser_ReturnsCreatedUser() {
        String email = "new@mail.com";
        String password = "123456";

        User createdUser = new User(
                1,
                email,
                BCrypt.hashpw(password, BCrypt.gensalt())
        );

        when(userDao.insert(any(User.class))).thenReturn(createdUser);

        User result = userService.createUser(email, password);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(email, result.getEmail());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userDao).insert(captor.capture());

        User passedToDao = captor.getValue();

        assertEquals(email, passedToDao.getEmail());

        assertTrue(
                BCrypt.checkpw(
                        password,
                        passedToDao.getHashPassword()
                )
        );
    }
}