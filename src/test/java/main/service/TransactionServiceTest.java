package main.service;

import main.entities.*;
import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import main.exceptions.ValidationException;
import main.repositories.AccountRepository;
import main.repositories.CategoryRepository;
import main.repositories.TransactionRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private final long accountId = 1L;
    private final long userId = 2L;
    private final long transactionId = 3L;
    private final long categoryId = 4L;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private TransactionService subj;

    @Nested
    @DisplayName("Method: findAllByAccId")
    class FindAllByAccIdTests {
        @Test
        void shouldReturnAllTransactionsWithCategoryWhereAccountIsInvolvedWhenUserIsAccountOwner() {
            Transaction mockTransaction1 = mock(Transaction.class);
            Transaction mockTransaction2 = mock(Transaction.class);
            List<Transaction> expectedList = List.of(mockTransaction1, mockTransaction2);
            Account mockAccount = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mockAccount));
            Mockito.when(mockAccount.isOwner(userId))
                    .thenReturn(true);
            Mockito.when(transactionRepository.findAllByAccountIdAndUserIdWithCategory(accountId, userId))
                    .thenReturn(expectedList);

            List<Transaction> res = subj.findAllByAccId(accountId, userId);

            assertThat(res).isEqualTo(expectedList);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenAccountNotFound() {
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.findAllByAccId(accountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Account")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(accountId));

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenExistingAccountDoesNotBelongToUser() {
            Account mockAccount = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mockAccount));
            Mockito.when(mockAccount.isOwner(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> subj.findAllByAccId(accountId, 2L))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verifyNoInteractions(transactionRepository);
        }
    }

    @Nested
    @DisplayName("Method: getTransactionById")
    class GetTransactionByIdTests {
        @ParameterizedTest(name = "fromOwner = {0}, toOwner = {1}")
        @CsvSource({
                "true, false",
                "true, true",
                "false, true"
        })
        void shouldReturnExistingTransactionWhenUserOwnsAtLeastOneAccount(boolean fromOwner, boolean toOwner) {
            Account fromAccount = mock(Account.class);
            Account toAccount = mock(Account.class);
            Transaction mockTransaction = mock(Transaction.class);
            Mockito.when(transactionRepository.findById(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            long userId = 2L;
            Mockito.when(fromAccount.isOwner(userId))
                    .thenReturn(fromOwner);
            Mockito.when(toAccount.isOwner(userId))
                    .thenReturn(toOwner);

            Transaction res = subj.getTransactionById(userId, transactionId);

            assertThat(res).isEqualTo(mockTransaction);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenUserDoesNotOwnAtLeastOneAccounts() {
            Account fromAccount = mock(Account.class);
            Account toAccount = mock(Account.class);
            Transaction mockTransaction = mock(Transaction.class);
            Mockito.when(transactionRepository.findById(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            Mockito.when(fromAccount.isOwner(userId))
                    .thenReturn(false);
            Mockito.when(toAccount.isOwner(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> subj.getTransactionById(userId, transactionId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTransactionDoesNotExist() {
            Mockito.when(transactionRepository.findById(transactionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.getTransactionById(userId, transactionId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Transaction")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(transactionId));
        }

    }

    @Nested
    @DisplayName("Method: createRegularTransaction")
    class CreateRegularTransactionTest {
        @Test
        void shouldThrowResourceNotFoundExceptionWhenAccountDoesNotExist() {
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.createRegularTransaction(
                    mock(TransactionType.class),
                    categoryId,
                    BigDecimal.ONE,
                    userId,
                    accountId
            ))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Account")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(accountId));

            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mock(Account.class)));
            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.createRegularTransaction(
                    mock(TransactionType.class),
                    categoryId,
                    BigDecimal.ONE,
                    userId,
                    accountId
            ))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Category")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(categoryId));

            verifyNoMoreInteractions(categoryRepository);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenUserDoesNotOwnCategory() {
            Category mockCategory = mock(Category.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mock(Account.class)));
            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(mockCategory));
            Mockito.when(mockCategory.isUserOwner(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> subj.createRegularTransaction(
                    mock(TransactionType.class),
                    categoryId,
                    BigDecimal.ONE,
                    userId,
                    accountId
            ))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verifyNoMoreInteractions(categoryRepository);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenUserDoesNotOwnAccount() {
            Account mockAccount = mock(Account.class);
            Category mockCategory = mock(Category.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mockAccount));
            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(mockCategory));
            Mockito.when(mockCategory.isUserOwner(userId))
                    .thenReturn(true);
            Mockito.when(mockAccount.isOwner(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> subj.createRegularTransaction(
                    mock(TransactionType.class),
                    categoryId,
                    BigDecimal.ONE,
                    userId,
                    accountId
            ))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verifyNoMoreInteractions(categoryRepository);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowValidationExceptionWhenExpenseExceedsAccountBalance() {
            Account mockAccount = new Account();
            User mockUser = mock(User.class);
            mockAccount.setUser(mockUser);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);
            mockAccount.setBalance(BigDecimal.valueOf(100));
            BigDecimal exceededAmount = BigDecimal.valueOf(200);
            Category mockCategory = mock(Category.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(mockAccount));
            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(mockCategory));
            Mockito.when(mockCategory.isUserOwner(userId))
                    .thenReturn(true);

            assertThatThrownBy(() -> subj.createRegularTransaction(TransactionType.EXPENSE,
                    categoryId,
                    exceededAmount,
                    userId,
                    accountId))
                    .isInstanceOf(ValidationException.class)
                    .extracting("errors")
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .containsKey("amount");

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldCreateExpenseTransactionWhenUserOwnsAccountAndCategory() {
            Account account = new Account();
            account.setBalance(BigDecimal.valueOf(100));
            BigDecimal validAmount = BigDecimal.valueOf(10);

            User mockUser = mock(User.class);
            account.setUser(mockUser);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);

            Category mockCategory = mock(Category.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(account));

            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(mockCategory));

            Mockito.when(mockCategory.isUserOwner(userId))
                    .thenReturn(true);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transaction res = subj.createRegularTransaction(TransactionType.EXPENSE, categoryId, validAmount, userId, accountId);


            assertThat(res.getFromAccount())
                    .isEqualTo(account);
            assertThat(res.getToAccount())
                    .isNull();
            assertThat(res.getTransactionType())
                    .isEqualTo(TransactionType.EXPENSE);
            assertThat(res.getAmount())
                    .isEqualTo(validAmount);

            assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(90));
        }

        @Test
        void shouldCreateIncomeTransactionWhenUserOwnsAccountAndCategory() {
            Account account = new Account();
            account.setBalance(BigDecimal.valueOf(100));
            BigDecimal validAmount = BigDecimal.valueOf(10);

            User mockUser = mock(User.class);
            account.setUser(mockUser);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);

            Category mockCategory = mock(Category.class);

            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(account));

            Mockito.when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(mockCategory));

            Mockito.when(mockCategory.isUserOwner(userId))
                    .thenReturn(true);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transaction res = subj.createRegularTransaction(TransactionType.INCOME, categoryId, validAmount, userId, accountId);

            verify(transactionRepository).save(any(Transaction.class));

            assertThat(res.getFromAccount())
                    .isNull();
            assertThat(res.getToAccount())
                    .isEqualTo(account);
            assertThat(res.getTransactionType())
                    .isEqualTo(TransactionType.INCOME);
            assertThat(res.getAmount())
                    .isEqualTo(validAmount);

            assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(110));
        }
    }
}