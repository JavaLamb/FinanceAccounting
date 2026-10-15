package main.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import main.entities.TransactionType;
import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import main.security.SecurityConfig;
import main.repositories.UserRepository;
import main.security.WithMockCustomUser;
import main.service.TransactionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    private final long userId = 1L;
    private final long accountId = 2L;
    private final long transactionId = 3L;
    private final long categoryId = 4L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionToTransactionResponseConverter converter;

    @Nested
    @DisplayName("GET /transactions/accounts/{accountId}")
    class GetTransactionByAccountTests {
        @Test
        @WithMockCustomUser
        void shouldReturn400WhenIdIsInvalidType() throws Exception {

            mockMvc.perform(get("/transactions/accounts/NOT_VALID_FORMAT")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("TYPE_MISMATCH"))
                    .andExpect(jsonPath("$.message")
                            .value("Parameter 'accountId' should be of type long"));

            verify(transactionService, never()).findAllByAccId(anyLong(), anyLong());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenAccountNotFound() throws Exception {
            when(transactionService.findAllByAccId(anyLong(), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Account",
                            String.valueOf(accountId)
                    ));

            mockMvc.perform(get("/transactions/accounts/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error")
                            .value("RESOURCE_NOT_FOUND"))
                    .andExpect(jsonPath("$.message")
                            .value("Account not found with id 2"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn403WhenAccountDoesNotBelongUser() throws Exception {
            when(transactionService.findAllByAccId(anyLong(), anyLong()))
                    .thenThrow(new BusinessLogicException(
                            "You must be the owner of account",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"
                    ));

            mockMvc.perform(get("/transactions/accounts/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error")
                            .value("ACCESS_DENIED"))
                    .andExpect(jsonPath("$.message")
                            .value("You must be the owner of account"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturnEmptyListWhenAccountHasNoTransactions() throws Exception {
            when(transactionService.findAllByAccId(accountId, userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/transactions/accounts/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(converter, never()).convert(any());
            verify(transactionService).findAllByAccId(accountId, userId);
        }

        @Test
        @WithMockCustomUser
        void shouldReturnListOfTransactions() throws Exception {
            Transaction transaction1 = mock(Transaction.class);
            Transaction transaction2 = mock(Transaction.class);

            when(transactionService.findAllByAccId(accountId, userId))
                    .thenReturn(List.of(transaction1, transaction2));
            TransactionResponse response1 = createTransactionResponseExpense(0, accountId);
            TransactionResponse response2 = createTransactionResponseExpense(1, accountId);
            when(converter.convert(transaction1))
                    .thenReturn(response1);
            when(converter.convert(transaction2))
                    .thenReturn(response2);

            mockMvc.perform(get("/transactions/accounts/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].id").value(0))
                    .andExpect(jsonPath("$[1].id").value(1))
                    .andExpect(jsonPath("$[0].fromAccountId").value(2))
                    .andExpect(jsonPath("$[1].fromAccountId").value(2))
                    .andExpect(jsonPath("$[0].categoryName").value("categoryName 0"))
                    .andExpect(jsonPath("$[1].categoryName").value("categoryName 1"))
                    .andExpect(jsonPath("$[0].amount").value(BigDecimal.valueOf(0)))
                    .andExpect(jsonPath("$[1].amount").value(BigDecimal.valueOf(1)));
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(get("/transactions/accounts/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /transactions/accounts/{accountId}")
    class CreateRegularTransactionTests {

    }

    @Nested
    @DisplayName("GET /transactions/{transactionId}")
    class GetTransactionByIdTest {

    }

    @Nested
    @DisplayName("POST /transactions/accounts/{accountId}/transfer")
    class CreateTransferTests {

    }

    @Nested
    @DisplayName("PATCH /transactions/{transactionId}")
    class ChangeAmountOfTransactionTests {

    }

    @Nested
    @DisplayName("DELETE /transactions/{transactionId}")
    class RollbackExistingTransactionTests {

    }

    private TransactionResponse createTransactionResponseExpense(long number, long accountId) {
        return new TransactionResponse(
                number,
                TransactionType.EXPENSE,
                accountId,
                null,
                "categoryName " + number,
                BigDecimal.valueOf(number),
                Instant.now()
        );
    }
}