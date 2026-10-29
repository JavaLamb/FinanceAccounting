package main;

import com.jayway.jsonpath.JsonPath;
import main.dto.Request.CreateRegularTransactionRequest;
import main.dto.Request.LoginRequest;
import main.entities.*;
import main.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jwt.secret=dGhpcy1pcy1hLXN1cGVyLXNlY3JldC1rZXktZm9yLWp3dC10b2tlbi1nZW5lcmF0aW9uLXdpdGgtMjU2LWJpdHM=",
        "jwt.expiration=9000000"
})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class IntegrationTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            "postgres:16-alpine")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    ObjectMapper om;

    private User testUser;
    private Account testAccount;
    private Category testCategory;
    private String jwt;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User(
                "username",
                encoder.encode("password")
        );
        userRepository.save(testUser);

        testAccount = new Account(
                "testAccount",
                testUser,
                AccountType.DEBIT
        );
        testAccount.setBalance(new BigDecimal(1000));
        accountRepository.save(testAccount);

        testCategory = new Category(
                "testCategory",
                testUser
        );
        categoryRepository.save(testCategory);

        LoginRequest loginRequest = new LoginRequest("username", "password");

        String loginResponse = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwt = JsonPath.read(loginResponse, "$.accessToken");
    }

    @Test
    @DisplayName("Creating an expense transaction, updates the account balance")
    void shouldCreateExpenseWithValidJwt() throws Exception {
        CreateRegularTransactionRequest request = new CreateRegularTransactionRequest(
                TransactionType.EXPENSE,
                testCategory.getId(),
                new BigDecimal(150)
        );

        mockMvc.perform(post("/transactions/accounts/{accountId}", testAccount.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.transactionType")
                        .value("EXPENSE"))
                .andExpect(jsonPath("$.categoryName")
                        .value(testCategory.getName()))
                .andExpect(jsonPath("$.amount")
                        .value(150.00));

        Account updatedAccount = accountRepository.findById(testAccount.getId())
                .orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualByComparingTo("850.00");
    }

    @Test
    void shouldReturn401WhenJwtInvalid() throws Exception {
        String invalidJwt = jwt + "err";
        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidJwt))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                        .value("JWT_TOKEN_INVALID"));
    }

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                        .value("UNAUTHORIZED"));
    }
}