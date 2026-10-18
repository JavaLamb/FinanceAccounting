package main.controllers;

import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Request.ChangeTransactionRequest;
import main.dto.Request.CreateRegularTransactionRequest;
import main.dto.Request.CreateTransferRequest;
import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import main.entities.TransactionType;
import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import main.exceptions.ValidationException;
import main.security.SecurityConfig;
import main.repositories.UserRepository;
import main.security.WithMockCustomUser;
import main.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
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
    private final long toAccountId = 5L;

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
            mockMvc.perform(get("/transactions/accounts/INVALID_TYPE")
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

        private static Stream<Arguments> provideInvalidDtoForNullCheck() {
            return Stream.of(
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    null, 1L, new BigDecimal("100.00")),
                            "Transaction type cannot be empty"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    TransactionType.INCOME, null, new BigDecimal("100.00")),
                            "CategoryId cannot be empty"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    TransactionType.INCOME, 1L, null),
                            "Amount cannot be empty")
            );
        }

        private static Stream<Arguments> provideInvalidAmount() {
            TransactionType tt = TransactionType.INCOME;
            long id = 4L;
            return Stream.of(
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    tt, id, BigDecimal.ZERO),
                            "Amount should be positive"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    tt, id, null),
                            "Amount cannot be empty"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    tt, id, BigDecimal.valueOf(10.123)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    tt, id, BigDecimal.valueOf(1000000000.00)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new CreateRegularTransactionRequest(
                                    tt, id, BigDecimal.valueOf(1000000.01)),
                            "Maximum amount of single transaction is 1_000_000")
            );
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenIdIsInvalidType() throws Exception {
            mockMvc.perform(post("/transactions/accounts/NOT_VALID_FORMAT")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("TYPE_MISMATCH"))
                    .andExpect(jsonPath("$.message")
                            .value("Parameter 'accountId' should be of type long"));

            verify(transactionService, never()).findAllByAccId(anyLong(), anyLong());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidDtoForNullCheck")
        @WithMockCustomUser
        void shouldReturn400WhenDtoFieldIsNull(CreateRegularTransactionRequest request, String message) throws Exception {
            String jsonBody = om.writeValueAsString(request);

            mockMvc.perform(post("/transactions/accounts/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString(message)));

            verify(converter, never()).convert(any());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidAmount")
        @WithMockCustomUser
        void shouldReturn400WhenAmountInvalid(CreateRegularTransactionRequest request, String message) throws Exception {
            String jsonBody = om.writeValueAsString(request);

            mockMvc.perform(post("/transactions/accounts/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString(message)));

            verify(converter, never()).convert(any());
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(post("/transactions/accounts/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenAccountNotFound() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);

            when(transactionService.createRegularTransaction(any(), anyLong(), any(), anyLong(), eq(accountId)))
                    .thenThrow(new ResourceNotFoundException(
                            "Account", String.valueOf(accountId)));

            mockMvc.perform(post("/transactions/accounts/" + accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Account not found with id " + accountId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenCategoryNotFound() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);

            when(transactionService.createRegularTransaction(any(), eq(categoryId), any(), anyLong(), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Category", String.valueOf(categoryId)));

            mockMvc.perform(post("/transactions/accounts/" + accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Category not found with id " + categoryId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn403WhenUserNotCategoryOwner() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);

            when(transactionService.createRegularTransaction(any(), anyLong(), any(), anyLong(), anyLong()))
                    .thenThrow(new BusinessLogicException(
                            "To create a transaction, you must be the category owner",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"));

            mockMvc.perform(post("/transactions/accounts/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(containsString("ACCESS_DENIED")))
                    .andExpect(jsonPath("$.message")
                            .value("To create a transaction, you must be the category owner"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn403WhenUserNotAccountOwner() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);

            when(transactionService.createRegularTransaction(any(), anyLong(), any(), anyLong(), anyLong()))
                    .thenThrow(new BusinessLogicException(
                            "To create a transaction, you must be the account owner",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"));

            mockMvc.perform(post("/transactions/accounts/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(containsString("ACCESS_DENIED")))
                    .andExpect(jsonPath("$.message")
                            .value("To create a transaction, you must be the account owner"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn200AndCreatedIncomeTransactionWhenValidIncome() throws Exception {
            CreateRegularTransactionRequest request = createRegularIncomeRequest(200);
            Transaction transaction = mock(Transaction.class);
            when(transactionService.createRegularTransaction(request.transactionType(), request.categoryId(), request.amount(), userId, accountId))
                    .thenReturn(transaction);
            when(converter.convert(transaction))
                    .thenReturn(createTransactionResponseIncome(999, accountId));

            mockMvc.perform(post("/transactions/accounts/" + accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(999))
                    .andExpect(jsonPath("$.toAccountId").value(accountId))
                    .andExpect(jsonPath("$.categoryName").value("categoryName 999"))
                    .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(999)));
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenExpenseAndAmountExceedsBalance() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);

            when(transactionService.createRegularTransaction(any(), anyLong(), any(), anyLong(), anyLong()))
                    .thenThrow(new ValidationException(
                            Map.of("amount",
                                    List.of("The amount of transaction exceeds the balance of account"))));

            mockMvc.perform(post("/transactions/accounts/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.amount")
                            .value("The amount of transaction exceeds the balance of account"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn200AndCreatedExpenseTransactionWhenValidExpense() throws Exception {
            CreateRegularTransactionRequest request = createRegularExpenseRequest(200);
            Transaction transaction = mock(Transaction.class);
            when(transactionService.createRegularTransaction(request.transactionType(), request.categoryId(), request.amount(), userId, accountId))
                    .thenReturn(transaction);
            when(converter.convert(transaction))
                    .thenReturn(createTransactionResponseExpense(999, accountId));

            mockMvc.perform(post("/transactions/accounts/" + accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(999))
                    .andExpect(jsonPath("$.fromAccountId").value(accountId))
                    .andExpect(jsonPath("$.categoryName").value("categoryName 999"))
                    .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(999)));
        }

    }

    @Nested
    @DisplayName("GET /transactions/{transactionId}")
    class GetTransactionByIdTest {

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenIdIsInvalidType() throws Exception {
            mockMvc.perform(get("/transactions/INVALID_TYPE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("TYPE_MISMATCH"))
                    .andExpect(jsonPath("$.message")
                            .value("Parameter 'transactionId' should be of type long"));

            verify(transactionService, never()).getTransactionById(anyLong(), anyLong());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenAccountNotFound() throws Exception {
            when(transactionService.getTransactionById(anyLong(), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Transaction",
                            String.valueOf(transactionId)
                    ));

            mockMvc.perform(get("/transactions/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error")
                            .value("RESOURCE_NOT_FOUND"))
                    .andExpect(jsonPath("$.message")
                            .value("Transaction not found with id " + transactionId));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn403WhenNoAccountsBelongToUser() throws Exception {
            when(transactionService.getTransactionById(anyLong(), anyLong()))
                    .thenThrow(new BusinessLogicException(
                            "Transaction info is available to the participant only",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"
                    ));

            mockMvc.perform(get("/transactions/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error")
                            .value("ACCESS_DENIED"))
                    .andExpect(jsonPath("$.message")
                            .value("Transaction info is available to the participant only"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturnFoundTransaction() throws Exception {
            Transaction transaction = mock(Transaction.class);

            when(transactionService.getTransactionById(userId, transactionId))
                    .thenReturn(transaction);
            TransactionResponse response = createTransactionResponseExpense(999, accountId);
            when(converter.convert(transaction))
                    .thenReturn(response);


            mockMvc.perform(get("/transactions/3")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(999))
                    .andExpect(jsonPath("$.fromAccountId").value(accountId))
                    .andExpect(jsonPath("$.categoryName").value("categoryName 999"))
                    .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(999)));
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(get("/transactions/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /transactions/accounts/{accountId}/transfer")
    class CreateTransferTests {
        private static Stream<Arguments> provideInvalidDtoForNullCheck() {
            return Stream.of(
                    Arguments.of(
                            new CreateTransferRequest(null, 4L, BigDecimal.valueOf(100)),
                            "Destination account cannot be empty"),
                    Arguments.of(
                            new CreateTransferRequest(2L, null, BigDecimal.valueOf(100)),
                            "CategoryId cannot be empty"),
                    Arguments.of(
                            new CreateTransferRequest(2L, 4L, null),
                            "Amount cannot be empty")
            );
        }

        private static Stream<Arguments> provideInvalidAmount() {
            long categoryId = 4L;
            long accountId = 2L;
            return Stream.of(
                    Arguments.of(
                            new CreateTransferRequest(
                                    accountId, categoryId, BigDecimal.ZERO),
                            "Amount should be positive"),
                    Arguments.of(
                            new CreateTransferRequest(
                                    accountId, categoryId, null),
                            "Amount cannot be empty"),
                    Arguments.of(
                            new CreateTransferRequest(
                                    accountId, categoryId, BigDecimal.valueOf(10.123)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new CreateTransferRequest(
                                    accountId, categoryId, BigDecimal.valueOf(1000000000.00)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new CreateTransferRequest(
                                    accountId, categoryId, BigDecimal.valueOf(1000000.01)),
                            "Maximum amount of single transfer is 1_000_000")
            );
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenIdIsInvalidType() throws Exception {
            mockMvc.perform(post("/transactions/accounts/INVALID_TYPE/transfer")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("TYPE_MISMATCH"))
                    .andExpect(jsonPath("$.message")
                            .value("Parameter 'accountId' should be of type long"));

            verify(transactionService, never()).createTransfer(anyLong(), any(), anyLong(), anyLong(), anyLong());
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(post("/transactions/accounts/{}/transfer", accountId))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidDtoForNullCheck")
        @WithMockCustomUser
        void shouldReturn400WhenDtoFieldIsNull(CreateTransferRequest request, String message) throws Exception {
            String jsonBody = om.writeValueAsString(request);

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString(message)));

            verify(converter, never()).convert(any());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidAmount")
        @WithMockCustomUser
        void shouldReturn400WhenAmountInvalid(CreateTransferRequest request, String message) throws Exception {
            String jsonBody = om.writeValueAsString(request);

            mockMvc.perform(post("/transactions/accounts/999/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString(message)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenFromAccountNotFound() throws Exception {
            long fromAccountId = accountId;
            CreateTransferRequest request = createTransferRequest(BigDecimal.valueOf(200), toAccountId);

            when(transactionService.createTransfer(any(), eq(BigDecimal.valueOf(200)), any(), eq(fromAccountId), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Account", String.valueOf(fromAccountId)));

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", fromAccountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Account not found with id " + fromAccountId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenCategoryNotFoundOrNotBelongToUser() throws Exception {
            CreateTransferRequest request = createTransferRequest(BigDecimal.valueOf(200), toAccountId);

            when(transactionService.createTransfer(any(), eq(BigDecimal.valueOf(200)), eq(categoryId), anyLong(), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Category", String.valueOf(categoryId)));

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Category not found with id " + categoryId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn403WhenUserNotFromAccountOwner() throws Exception {
            CreateTransferRequest request = createTransferRequest(BigDecimal.valueOf(200), toAccountId);

            when(transactionService.createTransfer(eq(toAccountId), eq(BigDecimal.valueOf(200)), any(), eq(accountId), anyLong()))
                    .thenThrow(new BusinessLogicException(
                            "The originating account must belong to the transfer originator",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"));

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(containsString("ACCESS_DENIED")))
                    .andExpect(jsonPath("$.message")
                            .value("The originating account must belong to the transfer originator"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenAmountExceedsFromAccountBalance() throws Exception {
            CreateTransferRequest request = createTransferRequest(BigDecimal.valueOf(200), toAccountId);

            when(transactionService.createTransfer(eq(toAccountId), eq(BigDecimal.valueOf(200)), any(), eq(accountId), anyLong()))
                    .thenThrow(new ValidationException(Map.of("amount",
                            List.of("The amount of transaction exceeds the balance of account"))));

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("VALIDATION_ERROR")))
                    .andExpect(jsonPath("$.message")
                            .value("Validation failed"))
                    .andExpect(jsonPath("$.validationErrors.amount")
                            .value("The amount of transaction exceeds the balance of account"));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenToAccountNotFound() throws Exception {
            long fromAccountId = accountId;
            CreateTransferRequest request = createTransferRequest(BigDecimal.valueOf(200), toAccountId);

            when(transactionService.createTransfer(eq(toAccountId), eq(BigDecimal.valueOf(200)), any(), eq(fromAccountId), anyLong()))
                    .thenThrow(new ResourceNotFoundException(
                            "Account", String.valueOf(toAccountId)));

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", fromAccountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Account not found with id " + toAccountId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn200AndCreatedTransfer() throws Exception {
            CreateTransferRequest request = createTransferRequest(BigDecimal.TEN, toAccountId);
            Transaction transaction = mock(Transaction.class);
            TransactionResponse response = TransactionResponse.builder()
                    .id(transactionId)
                    .transactionType(TransactionType.TRANSFER)
                    .fromAccountId(accountId)
                    .toAccountId(toAccountId)
                    .categoryName("categoryName")
                    .amount(BigDecimal.TEN)
                    .dateTime(Instant.now())
                    .build();
            when(transactionService.createTransfer(toAccountId, BigDecimal.TEN, categoryId, accountId, userId))
                    .thenReturn(transaction);
            when(converter.convert(transaction))
                    .thenReturn(response);

            mockMvc.perform(post("/transactions/accounts/{accountId}/transfer", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(transactionId))
                    .andExpect(jsonPath("$.transactionType").value(TransactionType.TRANSFER.name()))
                    .andExpect(jsonPath("$.toAccountId").value(toAccountId))
                    .andExpect(jsonPath("$.fromAccountId").value(accountId))
                    .andExpect(jsonPath("$.categoryName").value("categoryName"))
                    .andExpect(jsonPath("$.amount").value(BigDecimal.TEN));
        }
    }

    @Nested
    @DisplayName("PATCH /transactions/{transactionId}")
    class ChangeAmountOfTransactionTests {

        private static Stream<Arguments> provideInvalidAmount() {
            return Stream.of(
                    Arguments.of(
                            new ChangeTransactionRequest(
                                    BigDecimal.ZERO),
                            "Amount should be positive"),
                    Arguments.of(
                            new ChangeTransactionRequest(
                                    null),
                            "Amount cannot be empty"),
                    Arguments.of(
                            new ChangeTransactionRequest(
                                    BigDecimal.valueOf(10.123)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new ChangeTransactionRequest(
                                    BigDecimal.valueOf(1000000000.00)),
                            "Incorrect format of amount"),
                    Arguments.of(
                            new ChangeTransactionRequest(
                                    BigDecimal.valueOf(1000000.01)),
                            "Maximum amount of single transaction is 1_000_000")
            );
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenIdIsInvalidType() throws Exception {
            mockMvc.perform(patch("/transactions/INVALID_TYPE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error")
                            .value("TYPE_MISMATCH"))
                    .andExpect(jsonPath("$.message")
                            .value("Parameter 'transactionId' should be of type long"));

            verify(transactionService, never()).changeAmount(anyLong(), anyLong(), any(BigDecimal.class));
        }

        @Test
        void shouldReturn401WhenUnauthorized() throws Exception {
            mockMvc.perform(patch("/transactions/{transactionId}", transactionId))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidAmount")
        @WithMockCustomUser
        void shouldReturn400WhenAmountInvalid(ChangeTransactionRequest request, String message) throws Exception {
            String jsonBody = om.writeValueAsString(request);

            mockMvc.perform(patch("/transactions/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString(message)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn404WhenTransactionNotFound() throws Exception {
            ChangeTransactionRequest request = new ChangeTransactionRequest(BigDecimal.TEN);

            when(transactionService.changeAmount(anyLong(), anyLong(), any(BigDecimal.class)))
                    .thenThrow(new ResourceNotFoundException(
                            "Transaction",
                            String.valueOf(transactionId)
                    ));

            mockMvc.perform(patch("/transactions/{transactionId}", transactionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(
                            containsString("Transaction not found with id " + transactionId)));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenChangeLeadToNegativeBalanceOfAnyAccount() throws Exception {
            ChangeTransactionRequest request = new ChangeTransactionRequest(BigDecimal.TEN);

            when(transactionService.changeAmount(anyLong(), anyLong(), any(BigDecimal.class)))
                    .thenThrow(new ValidationException(
                            Map.of("Amount",
                                    List.of("Impossible to change transaction amount: balance of account "
                                            + "testAccountName will become negative"))));

            mockMvc.perform(patch("/transactions/{transactionId}", transactionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(
                            containsString("Impossible to change transaction amount: " +
                                    "balance of account testAccountName will become negative")))
                    .andExpect(content().string(containsString("VALIDATION_ERROR")));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn400WhenUserNotOwnAllAccounts() throws Exception {
            ChangeTransactionRequest request = new ChangeTransactionRequest(BigDecimal.TEN);

            when(transactionService.changeAmount(anyLong(), anyLong(), any(BigDecimal.class)))
                    .thenThrow(new BusinessLogicException(
                            "You must be the owner of both accounts",
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED"
                    ));

            mockMvc.perform(patch("/transactions/{transactionId}", transactionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(
                            containsString("ACCESS_DENIED")));

            verify(converter, never()).convert(any());
        }

        @Test
        @WithMockCustomUser
        void shouldReturn200AndChangedTransaction() throws Exception {
            ChangeTransactionRequest request = new ChangeTransactionRequest(BigDecimal.ONE);
            Transaction transaction = mock(Transaction.class);
            TransactionResponse response = TransactionResponse.builder()
                    .id(transactionId)
                    .transactionType(TransactionType.TRANSFER)
                    .fromAccountId(accountId)
                    .toAccountId(toAccountId)
                    .categoryName("categoryName")
                    .amount(BigDecimal.ONE)
                    .dateTime(Instant.now())
                    .build();
            when(transactionService.changeAmount(userId, transactionId, BigDecimal.ONE))
                    .thenReturn(transaction);
            when(converter.convert(transaction))
                    .thenReturn(response);

            mockMvc.perform(patch("/transactions/{transactionId}", transactionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(transactionId))
                    .andExpect(jsonPath("$.transactionType").value(TransactionType.TRANSFER.name()))
                    .andExpect(jsonPath("$.toAccountId").value(toAccountId))
                    .andExpect(jsonPath("$.fromAccountId").value(accountId))
                    .andExpect(jsonPath("$.categoryName").value("categoryName"))
                    .andExpect(jsonPath("$.amount").value(BigDecimal.ONE));
        }
    }

    @Nested
    @DisplayName("DELETE /transactions/{transactionId}")
    class RollbackExistingTransactionTests {

    }

    private CreateTransferRequest createTransferRequest(BigDecimal amount, long toAccountId) {
        return new CreateTransferRequest(
                toAccountId,
                categoryId,
                amount
        );
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

    private TransactionResponse createTransactionResponseIncome(long number, long accountId) {
        return new TransactionResponse(
                number,
                TransactionType.INCOME,
                null,
                accountId,
                "categoryName " + number,
                BigDecimal.valueOf(number),
                Instant.now()
        );
    }

    private CreateRegularTransactionRequest createRegularExpenseRequest(long number) {
        return new CreateRegularTransactionRequest(
                TransactionType.EXPENSE,
                categoryId,
                BigDecimal.valueOf(number));
    }

    private CreateRegularTransactionRequest createRegularIncomeRequest(long number) {
        return new CreateRegularTransactionRequest(
                TransactionType.INCOME,
                categoryId,
                BigDecimal.valueOf(number));
    }
}