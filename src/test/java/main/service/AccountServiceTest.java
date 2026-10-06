package main.service;

import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import main.repositories.AccountRepository;
import main.repositories.UserRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

            ResourceNotFoundException ex = assertThatThrownBy(() -> subj.deactivateAccount(accountId, userId))
                    .asInstanceOf(InstanceOfAssertFactories.type(ResourceNotFoundException.class))
                    .actual();

            assertThat(ex.getResourceId()).isEqualTo(String.valueOf(accountId));
            assertThat(ex.getResourceType()).isEqualTo("Account");
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

            BusinessLogicException ex = assertThatThrownBy(() -> subj.deactivateAccount(accountId, userId))
                    .asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
                    .actual();

            assertThat(ex.getErrorCode()).isEqualTo("ACCESS_DENIED");
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);

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

            ResourceNotFoundException ex = assertThatThrownBy(() -> subj.findByIdService(accountId, userId))
                    .asInstanceOf(InstanceOfAssertFactories.type(ResourceNotFoundException.class))
                    .actual();

            assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(ex.getResourceType()).isEqualTo("Account");
            assertThat(ex.getResourceId()).isEqualTo(String.valueOf(accountId));
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
            BusinessLogicException ex = assertThatThrownBy(() -> subj.createAccount(name, userId, AccountType.DEBIT))
                    .asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
                    .actual();

            assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ex.getErrorCode()).isEqualTo("ACCOUNT_LIMIT_EXCEEDED");

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


            ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
            verify(accountRepository).save(captor.capture());

            Account savedAccount = captor.getValue();

            assertThat(res).isEqualTo(savedAccount);
            assertThat(savedAccount.getName()).isEqualTo(expectedName);
            assertThat(savedAccount.getUser()).isEqualTo(excpectedUser);
            assertThat(savedAccount.getAccountType()).isEqualTo(expectedType);
        }
    }

    @Nested
    @DisplayName("Method: getAllByUserId")
    class GetAllByUserId{
        @Test
        void shouldReturnActiveAccountsForUser(){
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