package main.controllers;

import main.dto.Request.CreateAccountRequest;
import main.dto.Response.AccountsResponse;
import main.entities.Account;
import main.entities.AccountType;
import main.security.SecurityConfig;
import main.converters.AccountToAccountResponseConverter;
import main.repositories.UserRepository;
import main.security.WithMockCustomUser;
import main.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.endsWith;

import java.math.BigDecimal;
import java.util.List;

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

    @Nested
    @DisplayName("GET /accounts")
    class GetAccountsTests {
        @Test
        @WithMockCustomUser
        void shouldReturnEmptyListWhenUserHasNoAccounts() throws Exception {
            when(accountService.getAllByUserId(1L))
                    .thenReturn(List.of());

            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(accountService).getAllByUserId(1L);
        }
    }

    @Nested
    @DisplayName("POST /accounts")
    class CreateAccountTests {
        @Test
        @WithMockCustomUser
        void shouldReturnIIIIIIIIIIIIIII() throws Exception {
            Account account = mock(Account.class);
            when(account.getId()).thenReturn(10L);

            when(accountService.createAccount(
                    "My account", 1L, AccountType.SAVINGS
            )).thenReturn(account);

            AccountsResponse response = AccountsResponse.builder()
                    .id(10L)
                    .name("My account")
                    .balance(BigDecimal.ZERO)
                    .accountType(AccountType.SAVINGS)
                    .build();

            CreateAccountRequest request = new CreateAccountRequest(
                    "My account",
                    AccountType.SAVINGS
            );

            when(converter.convert(account)).thenReturn(response);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Location", endsWith("/accounts/10")))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.name").value("My account"))
                    .andExpect(jsonPath("$.balance").value(0))
                    .andExpect(jsonPath("$.accountType").value("SAVINGS"));

            verify(accountService).createAccount(
                    "My account", 1L, AccountType.SAVINGS
            );
        }
    }
}