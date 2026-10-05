package main.service;

import main.exceptions.BusinessLogicException;
import main.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.repositories.AccountRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final int accountLimit = 5;

    @Transactional
    public void deactivateAccount(long accountId, long userId) {
        Account accountToDeactivate = accountRepository.findByIdAndActiveTrue(accountId).orElseThrow(() -> new ResourceNotFoundException(
                "Account",
                String.valueOf(accountId)
        ));
        if (!accountToDeactivate.isOwner(userId)) throw new BusinessLogicException(
                "To delete account you must be the owner",
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED");

        accountToDeactivate.setActive(false);
    }

    @Transactional(readOnly = true)
    public Account findByIdService(long accountId, long userId) {
        return accountRepository.findByIdAndUserIdAndActiveTrue(accountId, userId)
                .orElseThrow(() -> new BusinessLogicException(
                        "Account not found or access denied",
                        HttpStatus.FORBIDDEN,
                        "ACCESS_DENIED"));
    }

    @Transactional
    public Account createAccount(String name, long userId, AccountType accType) {
        if (!canCreateMoreAccount(userId)) {
            throw new BusinessLogicException(
                    "Account limit exceeded",
                    HttpStatus.BAD_REQUEST,
                    "ACCOUNT_LIMIT_EXCEEDED");
        }
        User user = userRepository.getReferenceById(userId);
        return accountRepository.save(new Account(name, user, accType));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllByUserId(long userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId);
    }

    private boolean canCreateMoreAccount(long userId) {
        return accountRepository.countAllByUserIdAndActiveTrue(userId) < accountLimit;
    }
}
