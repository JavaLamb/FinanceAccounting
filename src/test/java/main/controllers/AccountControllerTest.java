package main.controllers;

import main.dto.Request.CreateAccountRequest;
import main.dto.Response.AccountsResponse;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.exceptions.BusinessLogicException;
import main.security.SecurityConfig;
import main.converters.AccountToAccountResponseConverter;
import main.repositories.UserRepository;
import main.security.WithMockCustomUser;
import main.service.AccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(SecurityConfig.class)
@WebMvcTest(AccountController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerTest {
    @MockitoBean
    UserRepository userRepository;
    @MockitoBean
    AccountService accountService;
    @MockitoBean
    AccountToAccountResponseConverter converter;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;

    long userId = 1L;

    @Nested
    @DisplayName("GET /accounts")
    class GetAccountsTests {

        @Test
        @WithMockCustomUser
        void shouldReturnEmptyListWhenUserHasNoAccounts() throws Exception {
            when(accountService.getAllByUserId(userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(accountService).getAllByUserId(userId);
        }

        @Test
        @WithMockCustomUser
        void shouldReturnListOfUserAccounts() throws Exception {
            Account account1 = mock(Account.class);
            Account account2 = mock(Account.class);

            when(accountService.getAllByUserId(userId))
                    .thenReturn(List.of(account1, account2));
            AccountsResponse response1 = createAccountResponse(0);
            AccountsResponse response2 = createAccountResponse(1);
            when(converter.convert(account1))
                    .thenReturn(response1);
            when(converter.convert(account2))
                    .thenReturn(response2);

            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].id").value(0))
                    .andExpect(jsonPath("$[1].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("My account " + 0))
                    .andExpect(jsonPath("$[1].name").value("My account " + 1))
                    .andExpect(jsonPath("$[0].balance").value(0))
                    .andExpect(jsonPath("$[1].balance").value(1))
                    .andExpect(jsonPath("$[0].accountType").value("SAVINGS"))
                    .andExpect(jsonPath("$[1].accountType").value("SAVINGS"));
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /accounts")
    class CreateAccountTests {
        private static Stream<String> provideInvalidNames() {
            return Stream.of(
                    "   ",
                    "",
                    null,
                    "A".repeat(256)
            );
        }

        @WithMockCustomUser
        @ParameterizedTest
        @MethodSource("provideInvalidNames")
        void shouldReturn400WhenNameIsInvalid(String invalidName) throws Exception {
            CreateAccountRequest invalidRequest = new CreateAccountRequest(invalidName, AccountType.SAVINGS);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(accountService);
        }

        @WithMockCustomUser
        @Test
        void shouldReturn400WhenAccountTypeIsInvalid() throws Exception {
            String invalidAccountTypeJson = """
                    {
                        "name": "Valid name",
                        "accountType": "INVALID_ENUM_VALUE"
                    }
                    """;

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidAccountTypeJson))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(accountService);
        }

        @WithMockCustomUser
        @Test
        void shouldReturn400WhenExceededCountOfAccount() throws Exception {
            when(accountService.createAccount(
                    anyString(),
                    anyLong(),
                    any(AccountType.class)
            )).thenThrow(new BusinessLogicException(
                    "Account limit exceeded",
                    HttpStatus.BAD_REQUEST,
                    "ACCOUNT_LIMIT_EXCEEDED"
            ));
            CreateAccountRequest request = new CreateAccountRequest("name", AccountType.SAVINGS);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("ACCOUNT_LIMIT_EXCEEDED"));
        }

        @Test
        @WithMockCustomUser
        void shouldReturn201AndCorrectUrlToNewAccount() throws Exception {
            long number = 10L;
            Account account = mock(Account.class);
            when(account.getId()).thenReturn(number);

            when(accountService.createAccount(
                    "My account " + number, userId, AccountType.SAVINGS
            )).thenReturn(account);

            AccountsResponse response = createAccountResponse(number);

            CreateAccountRequest request = new CreateAccountRequest(
                    "My account " + number,
                    AccountType.SAVINGS
            );

            when(converter.convert(account)).thenReturn(response);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Location", Matchers.endsWith("/accounts/" + number)))
                    .andExpect(jsonPath("$.id").value(number))
                    .andExpect(jsonPath("$.name").value("My account " + number))
                    .andExpect(jsonPath("$.balance").value(number))
                    .andExpect(jsonPath("$.accountType").value("SAVINGS"));

            verify(accountService).createAccount(
                    "My account " + number, userId, AccountType.SAVINGS
            );
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(post("/accounts"))
                    .andExpect(status().isUnauthorized());
        }

    }

    private AccountsResponse createAccountResponse(long number) {
        return new AccountsResponse(
                number,
                "My account " + number,
                BigDecimal.valueOf(number),
                AccountType.SAVINGS);
    }

    private Account createTestAccount(long number, User user) {
        return new Account().builder()
                .id(number)
                .name("name" + number)
                .balance(BigDecimal.valueOf(number))
                .user(user)
                .accountType(AccountType.SAVINGS)
                .build();
    }
}