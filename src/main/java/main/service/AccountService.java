package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.exceptions.AccountException;
import main.exceptions.AuthException;
import main.repositories.AccountRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    int accountLimit = 5;

    public List<Account> findAllByUserId(long Userid) {
        return accountRepository.findByUserId(Userid);
    }

    public boolean canCreateMoreAccount(long id) {
        long result = accountRepository.countAllByUserId(id);
        if (result < 0) {
            return false;
        }
        return result < accountLimit;
    }

    public Account findByIdService(long accountId, long userId) throws AccountNotFoundException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        if (account.getUser().getId() == userId) {
            return account;
        }
        throw new AccountException("Access not allowed");
    }


    public boolean isExist(long id) {
        return accountRepository.findById(id).isPresent();
    }

    @Transactional
    public Account createAccount(String name, long userId, AccountType accType) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));
        if (!canCreateMoreAccount(currentUser.getId())) {
            throw new AccountException("Account limit exceeded");
        }
        return accountRepository.save(new Account(name, currentUser, accType));
    }

    public List<Account> getAllByUserId(long id) {
        return accountRepository.findByUserId(id);
    }

}
