package main.service;

import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import main.repositories.AccountRepository;
import main.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AccountService subj;

    @Nested
    @DisplayName("Method: deactivateAccount")
    class DeactivateAccountTests {
        @Test
        void shouldThrowResourceNotFoundExceptionWhenAccountNotFound() {
            long accountId = 1;
            long userId = 1;
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.deactivateAccount(accountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(accountId))
                    .hasFieldOrPropertyWithValue("resourceType", "Account");
        }

        @Test
        void shouldThrowBusinessLogicExceptionWhenAccountFoundButUserNotOwner() {
            long accountId = 1;
            long userId = 1;
            Account accountToDeactivate = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(accountToDeactivate));
            Mockito.when(accountToDeactivate.isOwner(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> subj.deactivateAccount(accountId, userId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCESS_DENIED")
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);

            verify(accountToDeactivate, never()).setActive(anyBoolean());
        }

        @Test
        void shouldSetActiveFalseSuccessfullyWhenAccountFoundAndUserIsOwner() {
            long accountId = 1L;
            long userId = 1L;
            Account accountToDeactivate = mock(Account.class);
            Mockito.when(accountRepository.findByIdAndActiveTrue(accountId))
                    .thenReturn(Optional.of(accountToDeactivate));
            Mockito.when(accountToDeactivate.isOwner(userId))
                    .thenReturn(true);

            subj.deactivateAccount(accountId, userId);

            verify(accountToDeactivate).setActive(false);
        }
    }

    @Nested
    @DisplayName("Method: findByIdService")
    class FindByIdTests {
        @Test
        void shouldThrowResourceNotFoundExceptionWhenAccountNotFoundOrUserNotOwner() {
            long accountId = 1L;
            long userId = 1L;
            Mockito.when(accountRepository.findByIdAndUserIdAndActiveTrue(accountId, userId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> subj.findByIdService(accountId, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasFieldOrPropertyWithValue("resourceType", "Account")
                    .hasFieldOrPropertyWithValue("resourceId", String.valueOf(accountId));
        }

        @Test
        void shouldReturnAccountWhenItFoundAndUserIsOwner() {
            long accountId = 1L;
            long userId = 2L;
            Account accountToShow = new Account(
                    1L,
                    "name",
                    BigDecimal.ZERO,
                    mock(User.class),
                    AccountType.DEBIT,
                    true);
            Mockito.when(accountRepository.findByIdAndUserIdAndActiveTrue(accountId, userId))
                    .thenReturn(Optional.of(accountToShow));

            Account res = subj.findByIdService(accountId, userId);

            assertThat(res).isEqualTo(accountToShow);
        }
    }

    @Nested
    @DisplayName("Method: createAccount")
    class CreateAccountTests {
        @Test
        void shouldThrowBusinessLogicExceptionWhenAccountLimitExceeded() {
            long userId = 1L;
            int exceededLimit = 5;
            String name = "name";
            Mockito.when(accountRepository.countAllByUserIdAndActiveTrue(userId))
                    .thenReturn(exceededLimit);
            assertThatThrownBy(() -> subj.createAccount(name, userId, AccountType.DEBIT))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasFieldOrPropertyWithValue("errorCode", "ACCOUNT_LIMIT_EXCEEDED");

            verifyNoInteractions(userRepository);
            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldSuccessfullyCreateAccountWhenWithinLimit() {
            long userId = 1L;
            int withinLimit = 4;
            String expectedName = "name";
            User excpectedUser = mock(User.class);
            AccountType expectedType = AccountType.SAVINGS;
            Mockito.when(accountRepository.countAllByUserIdAndActiveTrue(userId))
                    .thenReturn(withinLimit);
            Mockito.when(userRepository.getReferenceById(userId)).thenReturn(excpectedUser);
            Mockito.when(accountRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Account res = subj.createAccount(expectedName, userId, expectedType);


            assertThat(res.getName()).isEqualTo(expectedName);
            assertThat(res.getUser()).isEqualTo(excpectedUser);
            assertThat(res.getAccountType()).isEqualTo(expectedType);
        }
    }

    @Nested
    @DisplayName("Method: getAllByUserId")
    class GetAllByUserId {
        @Test
        void shouldReturnActiveAccountsForUser() {
            long userId = 1L;
            Account account1 = mock(Account.class);
            Account account2 = mock(Account.class);
            List<Account> expected = List.of(account1, account2);
            when(accountRepository.findByUserIdAndActiveTrue(userId)).thenReturn(expected);

            List<Account> res = subj.getAllByUserId(userId);

            assertThat(res).isEqualTo(expected);
        }
    }
}