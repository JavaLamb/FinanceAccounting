package main.service;

import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import main.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    int accountLimit = 5;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

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
    public Optional<Account> findByIdService(long id){
        return accountRepository.findById(id);
    }

    public boolean isExist(long id) {
        return accountRepository.findById(id).isPresent();
    }

    public void createAccount(User user, AccountType accType, String name, BigDecimal balance) {
        accountRepository.save(new Account(name, user, accType, balance));
    }

    public List<Account> getAllByUserId(long id) {
        return accountRepository.findByUserId(id);
    }

//    public BigDecimal getBalance(int accountId) {
//        return accountDao.findById(accountId).getBalance();
//    }
}
