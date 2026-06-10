package main.service;

import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.entities.Account;
import main.entities.Transaction;
import main.entities.TransactionCategory;
import main.entities.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TransactionService {
    private final TransactionDao transactionDao;
    private final AccountService accountService;

    public TransactionService(TransactionDao transactionDao, AccountService accountService) {
        this.transactionDao = transactionDao;
        this.accountService = accountService;
    }

    public void createTransaction(TransactionType tt, Account ownerAccount, BigDecimal amount, int selectedCategory) {
        switch (tt) {
            case TransactionType.INCOME -> income(ownerAccount, amount, selectedCategory);
            case TransactionType.EXPENSE -> expense(ownerAccount, amount, selectedCategory);
        }
    }

    public void createTransaction(Account ownerAccount, int recipientAccId, BigDecimal amount, int selectedCategory) {
        transfer(ownerAccount, recipientAccId, amount, selectedCategory);
    }


    private void income(Account ownerAccount, BigDecimal amount, int selectedCategory) {

        BigDecimal updatedBalance = ownerAccount.getBalance().add(amount);
        int categoryId = selectedCategory;
        transactionDao.insert(new Transaction(TransactionType.INCOME,ownerAccount.getId(),categoryId,amount));
        ownerAccount.setBalance(updatedBalance);
        accountService.accountDao.update(ownerAccount);
    }

    private void expense(Account ownerAccount, BigDecimal amount, int selectedCategory) {
        if (isPossible(ownerAccount, amount)) {
            int categoryId = selectedCategory;
            transactionDao.insert(new Transaction(TransactionType.EXPENSE, ownerAccount.getId(),categoryId,amount));
            BigDecimal updatedBalance = ownerAccount.getBalance().subtract(amount);
            ownerAccount.setBalance(updatedBalance);
            accountService.accountDao.update(ownerAccount);
            System.out.println("Expense Transaction done");
        } else {
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private void transfer(Account ownerAccount, int recipientAccId, BigDecimal amount, int selectedCategory) {
        if (isPossible(ownerAccount, amount)) {
            expense(ownerAccount, amount, selectedCategory);
            income(accountService.accountDao.findById(recipientAccId), amount, selectedCategory);
        } else {
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private boolean isPossible(Account account, BigDecimal amount) {
        return (accountService.getBalance(account.getId()).compareTo(amount)) >= 0;
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

    public List<TransactionCategory> showCategories(int userid){
        return transactionDao.showCategoriesDao(userid);
    }

}
