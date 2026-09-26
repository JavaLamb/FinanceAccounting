package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.exceptions.AccountException;
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
    private final int accountLimit = 5;

    public boolean canCreateMoreAccount(long id) {
        return accountRepository.countAllByUserIdAndActiveTrue(id) < accountLimit;
    }

    @Transactional
    public void deactivateAccount(long accountId, long userId){
        int res = accountRepository.deactivateByIdAndUserId(accountId, userId);
        if(res == 0){
            throw new AccountException("nothing changed");
        }
    }

    @Transactional
    public Account findByIdService(long accountId, long userId) throws AccountNotFoundException {
        Account account = accountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(AccountNotFoundException::new);
        if (account.getUser().getId() == userId) {
            return account;
        }
        throw new AccountException("Access not allowed");
    }

    @Transactional
    public Account createAccount(String name, long userId, AccountType accType) {
        if (!canCreateMoreAccount(userId)) {
            throw new AccountException("Account limit exceeded");
        }
        User user = userRepository.getReferenceById(userId);
        return accountRepository.save(new Account(name, user, accType));
    }

    public List<Account> getAllByUserId(long userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId);
    }

}
