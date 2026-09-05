package main.service;

import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.entities.Account;
import main.entities.Transaction;
import main.entities.TransactionCategory;
import main.entities.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Service
public class TransactionService {
    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

    public TransactionService(TransactionDao transactionDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    public void createTransaction(TransactionType tt, Account ownerAccount, BigDecimal amount, TransactionCategory category) {
        Transaction transaction = new Transaction();
                case INCOME -> income(ownerAccount, amount, category);

                case EXPENSE -> expense(ownerAccount, amount, category);
    }

    public void createTransaction(Account ownerAccount, Account recipientAccount, BigDecimal amount, int selectedCategory) {
        try {
            transactionManager.begin();

            transfer(ownerAccount, recipientAccount, amount, selectedCategory);

            transactionManager.commit();
        } catch (Exception e) {
            try {
                transactionManager.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }


    private void income(Account ownerAccount, BigDecimal amount, int selectedCategory) {
        BigDecimal updatedBalance = ownerAccount.getBalance().add(amount);
        int categoryId = selectedCategory;
        transactionDao.insert(new Transaction(TransactionType.INCOME, ownerAccount.getId(), categoryId, amount));
        ownerAccount.setBalance(updatedBalance);
        accountDao.update(ownerAccount);
    }

    private void expense(Account ownerAccount, BigDecimal amount, int selectedCategory) {
        if (isPossible(ownerAccount, amount)) {
            int categoryId = selectedCategory;
            transactionDao.insert(new Transaction(TransactionType.EXPENSE, ownerAccount.getId(), categoryId, amount));
            BigDecimal updatedBalance = ownerAccount.getBalance().subtract(amount);
            ownerAccount.setBalance(updatedBalance);
            accountDao.update(ownerAccount);
            System.out.println("Expense Transaction done");
        } else {
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private void transfer(Account ownerAccount, Account recipientAccount, BigDecimal amount, int selectedCategory) {
        if (isPossible(ownerAccount, amount)) {
            expense(ownerAccount, amount, selectedCategory);
            income(recipientAccount, amount, selectedCategory);
        } else {
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private boolean isPossible(Account account, BigDecimal amount) {
        return (accountDao.findById(account.getId()).getBalance().compareTo(amount)) >= 0;
    }

    public BigDecimal readBigDecimal(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                input = input.replace(',', '.');
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount have to be greater than 0. Try again.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("number format exception :(");
            }
        }
    }

    public List<TransactionCategory> showCategories(int userid) {
        return transactionDao.showCategoriesDao(userid);
    }

}
