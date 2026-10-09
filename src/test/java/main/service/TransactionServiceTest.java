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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;
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

    private Account createAccount(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(100));
        return account;
    }

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

        static Stream<Arguments> ownAtLeastOneExistingAccount() {
            return Stream.of(
                    Arguments.of(true, true, true, false),
                    Arguments.of(true, true, true, true),
                    Arguments.of(true, false, true, true),
                    Arguments.of(true, true, false, false),
                    Arguments.of(false, false, true, true)
            );
        }

        @ParameterizedTest(name = "fromExists = {0}, fromOwner = {1}, toExists = {2},  toOwner = {3}")
        @MethodSource("ownAtLeastOneExistingAccount")
        void shouldReturnExistingTransactionWhenUserOwnsAtLeastOneAccount(boolean fromExists,
                                                                          boolean fromOwner,
                                                                          boolean toExists,
                                                                          boolean toOwner) {
            Account fromAccount = fromExists ? mock(Account.class) : null;
            Account toAccount = toExists ? mock(Account.class) : null;
            Transaction mockTransaction = mock(Transaction.class);
            Mockito.when(transactionRepository.findById(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            if (fromAccount != null) {
                Mockito.when(fromAccount.isOwner(userId))
                        .thenReturn(fromOwner);
            }
            if (toAccount != null) {
                Mockito.when(toAccount.isOwner(userId))
                        .thenReturn(toOwner);
            }

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

            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(90));
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

            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(110));
        }
    }

    @Nested
    @DisplayName("Method: createTransfer")
    class CreateTransferTests {
        long fromAccountId = 20L;
        long toAccountId = 10L;

        @Test
        void shouldThrowResourceNotFoundExceptionWhenFromAccountDoesNotFound() {
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.createTransfer(toAccountId, BigDecimal.ONE, categoryId, fromAccountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Account")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(fromAccountId));

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExistOrNotOwnedByUser() {
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.of(mock(Account.class)));
            Mockito.when(categoryRepository.findByIdAndUserId(categoryId, userId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.createTransfer(toAccountId, BigDecimal.ONE, categoryId, fromAccountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Category")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(categoryId));

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenFromAccountNotOwnedByUser() {
            Account fromMockAccount = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.of(fromMockAccount));
            Mockito.when(fromMockAccount.isOwner(userId))
                    .thenReturn(false);
            Mockito.when(categoryRepository.findByIdAndUserId(categoryId, userId))
                    .thenReturn(Optional.of(mock(Category.class)));

            assertThatThrownBy(() -> subj.createTransfer(toAccountId, BigDecimal.ONE, categoryId, fromAccountId, userId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowValidationExceptionWhenAmountExceedsBalanceOfFromAccount() {
            Account fromAccount = new Account();
            User mockUser = mock(User.class);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);
            fromAccount.setBalance(BigDecimal.ONE);
            fromAccount.setUser(mockUser);
            BigDecimal exceededAmount = fromAccount.getBalance().add(BigDecimal.ONE);
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.of(fromAccount));
            Mockito.when(categoryRepository.findByIdAndUserId(categoryId, userId))
                    .thenReturn(Optional.of(mock(Category.class)));

            assertThatThrownBy(() -> subj.createTransfer(toAccountId, exceededAmount, categoryId, fromAccountId, userId))
                    .isInstanceOf(ValidationException.class)
                    .extracting("errors")
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .containsKey("amount");

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenToAccountDoesNotFound() {
            Account mockFromAccount = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.of(mockFromAccount));
            Mockito.when(categoryRepository.findByIdAndUserId(categoryId, userId))
                    .thenReturn(Optional.of(mock(Category.class)));
            Mockito.when(mockFromAccount.isOwner(userId))
                    .thenReturn(true);
            Mockito.when(mockFromAccount.getBalance())
                    .thenReturn(BigDecimal.TEN);
            Mockito.when(accountRepository.findByIdAndActiveTrue(toAccountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.createTransfer(toAccountId, BigDecimal.ONE, categoryId, fromAccountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Account")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(toAccountId));

            verifyNoInteractions(transactionRepository);
        }

        @Test
        void shouldCreateTransferWhenAmountValidAndUserOwnsFromAccount() {
            Account fromAccount = new Account();
            fromAccount.setBalance(BigDecimal.TEN);
            User mockUser = mock(User.class);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);
            fromAccount.setUser(mockUser);
            Mockito.when(accountRepository.findByIdAndActiveTrue(fromAccountId))
                    .thenReturn(Optional.of(fromAccount));
            Mockito.when(categoryRepository.findByIdAndUserId(categoryId, userId))
                    .thenReturn(Optional.of(mock(Category.class)));
            Account toAccount = new Account();
            toAccount.setBalance(BigDecimal.ZERO);
            Mockito.when(accountRepository.findByIdAndActiveTrue(toAccountId))
                    .thenReturn(Optional.of(toAccount));
            Mockito.when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transaction res = subj.createTransfer(toAccountId, BigDecimal.ONE, categoryId, fromAccountId, userId);

            assertThat(fromAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(9));
            assertThat(toAccount.getBalance()).isEqualByComparingTo(BigDecimal.ONE);

            assertThat(res.getToAccount()).isEqualTo(toAccount);
            assertThat(res.getFromAccount()).isEqualTo(fromAccount);
            assertThat(res.getTransactionType()).isEqualTo(TransactionType.TRANSFER);

            verify(transactionRepository).save(any(Transaction.class));
        }
    }

    @Nested
    @DisplayName("Method: changeAmount")
    class ChangeAmountTests {

        static Stream<Arguments> doesNotOwnsAllExistingAccounts() {
            return Stream.of(
                    Arguments.of(true, true, true, false),
                    Arguments.of(true, false, true, true),
                    Arguments.of(false, false, true, false),
                    Arguments.of(true, false, false, false),
                    Arguments.of(true, false, true, false)
            );
        }

        static Stream<Arguments> ownsAllExistingAccounts() {
            return Stream.of(
                    Arguments.of(true, false),
                    Arguments.of(false, true),
                    Arguments.of(true, true)
            );
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTransactionDoesNotFound() {
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.changeAmount(userId, transactionId, BigDecimal.ONE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Transaction")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(transactionId));
        }

        @ParameterizedTest(name = "fromExists = {0}, fromOwner = {1}, toExists = {2},  toOwner = {3}")
        @MethodSource("doesNotOwnsAllExistingAccounts")
        void shouldThrowBusinessLogicExceptionWhenUserDoesNotOwnsAllExistingAccounts(
                boolean fromExists,
                boolean fromOwner,
                boolean toExists,
                boolean toOwner) {
            Transaction mockTransaction = mock(Transaction.class);
            Account fromAccount = fromExists ? mock(Account.class) : null;
            Account toAccount = toExists ? mock(Account.class) : null;
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getAmount())
                    .thenReturn(BigDecimal.TEN);
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            if (fromAccount != null) {
                Mockito.when(fromAccount.isOwner(userId))
                        .thenReturn(fromOwner);
            }
            if (toAccount != null) {
                Mockito.lenient().when(toAccount.isOwner(userId))
                        .thenReturn(toOwner);
            }

            assertThatThrownBy(() -> subj.changeAmount(userId, transactionId, BigDecimal.ONE))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verify(mockTransaction, never()).setAmount(any());
        }

        @Test
        void shouldThrowValidationExceptionWhenFromAccountBalanceGoesNegative() {
            Transaction mockTransaction = mock(Transaction.class);
            Account fromAccount = new Account();
            BigDecimal oldAmount = BigDecimal.TWO;
            BigDecimal exceededAmount = BigDecimal.TEN;
            fromAccount.setBalance(BigDecimal.ZERO);
            User mockUser = mock(User.class);
            fromAccount.setUser(mockUser);
            Account toAccount = mock(Account.class);
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getAmount())
                    .thenReturn(oldAmount);
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);
            Mockito.when(toAccount.isOwner(userId))
                    .thenReturn(true);

            assertThatThrownBy(() -> subj.changeAmount(userId, transactionId, exceededAmount))
                    .isInstanceOf(ValidationException.class)
                    .extracting("errors")
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .containsKey("Amount");

            assertThat(fromAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

            verify(mockTransaction, never()).setAmount(any());
        }

        @Test
        void shouldThrowValidationExceptionWhenToAccountBalanceGoesNegative() {
            Transaction mockTransaction = mock(Transaction.class);
            User mockUser = mock(User.class);

            Account toAccount = new Account();
            toAccount.setBalance(BigDecimal.ZERO);
            toAccount.setUser(mockUser);

            Account fromAccount = new Account();
            fromAccount.setBalance(BigDecimal.valueOf(100));
            fromAccount.setUser(mockUser);

            BigDecimal oldAmount = BigDecimal.TEN;
            BigDecimal exceededAmount = BigDecimal.TWO;

            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getAmount())
                    .thenReturn(oldAmount);
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            Mockito.when(mockUser.getId())
                    .thenReturn(userId);

            assertThatThrownBy(() -> subj.changeAmount(userId, transactionId, exceededAmount))
                    .isInstanceOf(ValidationException.class)
                    .extracting("errors")
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .containsKey("Amount");

            verify(mockTransaction, never()).setAmount(any());
        }

        @ParameterizedTest(name = "fromExists = {0}, toExists = {1}")
        @MethodSource("ownsAllExistingAccounts")
        void shouldChangeAmountWhenUserOwnsAllAccountsAndNewAmountIsValid(
                boolean fromExists,
                boolean toExists) {

            User owner = mock(User.class);
            when(owner.getId()).thenReturn(userId);

            Account fromAccount = fromExists ? createAccount(owner) : null;
            Account toAccount = toExists ? createAccount(owner) : null;

            Transaction transaction = new Transaction();
            transaction.setAmount(BigDecimal.TEN);
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);

            when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(transaction));

            Transaction result = subj.changeAmount(
                    userId, transactionId, BigDecimal.ONE
            );

            assertThat(result.getAmount())
                    .isEqualByComparingTo(BigDecimal.ONE);

            if (fromAccount != null) {
                assertThat(fromAccount.getBalance())
                        .isEqualByComparingTo(BigDecimal.valueOf(109));
            }

            if (toAccount != null) {
                assertThat(toAccount.getBalance())
                        .isEqualByComparingTo(BigDecimal.valueOf(91));
            }
        }
    }

    @Nested
    @DisplayName("Method: deleteTransaction")
    class DeleteTransactionTests {

        static Stream<Arguments> doesNotOwnsAllExistingAccounts() {
            return Stream.of(
                    Arguments.of(true, true, true, false),
                    Arguments.of(true, false, true, true),
                    Arguments.of(false, false, true, false),
                    Arguments.of(true, false, false, false),
                    Arguments.of(true, false, true, false)
            );
        }

        static Stream<Arguments> ownsAllExistingAccounts() {
            return Stream.of(
                    Arguments.of(true, false),
                    Arguments.of(false, true),
                    Arguments.of(true, true)
            );
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenTransactionDoesNotExist() {
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.deleteTransaction(transactionId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceType", "Transaction")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(transactionId));

            verifyNoMoreInteractions(transactionRepository);
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenToAccountBalanceGoesNegative() {
            Transaction mockTransaction = mock(Transaction.class);
            Account toAccount = mock(Account.class);
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getAmount())
                    .thenReturn(BigDecimal.TEN);
            Mockito.when(mockTransaction.getFromAccount())
                            .thenReturn(null);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            Mockito.when(toAccount.isOwner(userId))
                    .thenReturn(true);
            Mockito.when(toAccount.getBalance())
                    .thenReturn(BigDecimal.ZERO);

            assertThatThrownBy(()-> subj.deleteTransaction(transactionId, userId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasFieldOrPropertyWithValue("errorCode", "INSUFFICIENT_FUNDS");

            verifyNoMoreInteractions(transactionRepository);
        }

        @ParameterizedTest(name = "fromExists = {0}, fromOwner = {1}, toExists = {2},  toOwner = {3}")
        @MethodSource("doesNotOwnsAllExistingAccounts")
        void shouldThrowBusinessLogicExceptionWhenUserDoesNotOwnsAllExistingAccounts(
                boolean fromExists,
                boolean fromOwner,
                boolean toExists,
                boolean toOwner) {
            Transaction mockTransaction = mock(Transaction.class);
            Account fromAccount = fromExists ? mock(Account.class) : null;
            Account toAccount = toExists ? mock(Account.class) : null;
            Mockito.when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(mockTransaction));
            Mockito.when(mockTransaction.getAmount())
                    .thenReturn(BigDecimal.TEN);
            Mockito.when(mockTransaction.getFromAccount())
                    .thenReturn(fromAccount);
            Mockito.when(mockTransaction.getToAccount())
                    .thenReturn(toAccount);
            if (fromAccount != null) {
                Mockito.when(fromAccount.isOwner(userId))
                        .thenReturn(fromOwner);
            }
            if (toAccount != null) {
                Mockito.lenient().when(toAccount.isOwner(userId))
                        .thenReturn(toOwner);
            }

            assertThatThrownBy(() -> subj.changeAmount(userId, transactionId, BigDecimal.ONE))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED");

            verify(transactionRepository, never()).delete(any());
        }

        @ParameterizedTest(name = "fromExists = {0}, toExists = {1}")
        @MethodSource("ownsAllExistingAccounts")
        void shouldChangeAmountWhenUserOwnsAllAccountsAndNewAmountIsValid(
                boolean fromExists,
                boolean toExists) {

            User owner = mock(User.class);
            when(owner.getId()).thenReturn(userId);

            Account fromAccount = fromExists ? createAccount(owner) : null;
            Account toAccount = toExists ? createAccount(owner) : null;

            Transaction transaction = new Transaction();
            transaction.setAmount(BigDecimal.TEN);
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);

            when(transactionRepository.findByIdWithAccounts(transactionId))
                    .thenReturn(Optional.of(transaction));

            subj.deleteTransaction(
                    transactionId, userId
            );

            if (fromAccount != null) {
                assertThat(fromAccount.getBalance())
                        .isEqualByComparingTo(BigDecimal.valueOf(110));
            }

            if (toAccount != null) {
                assertThat(toAccount.getBalance())
                        .isEqualByComparingTo(BigDecimal.valueOf(90));
            }
        }
    }
}