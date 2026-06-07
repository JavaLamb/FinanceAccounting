package main.service;

import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.entities.Account;
import main.entities.TransactionType;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransactionService {
    private final TransactionDao transactionDao;
    private final AccountService accountService;

    public TransactionService(TransactionDao transactionDao, AccountService accountService) {
        this.transactionDao = transactionDao;
        this.accountService = accountService;
    }

    public void createTransaction(TransactionType tt, Account ownerAccount, BigDecimal amount) {
        switch (tt) {
            case TransactionType.INCOME -> income(ownerAccount, amount);
            case TransactionType.EXPENSE -> expense(ownerAccount, amount);
        }
    }

    public void createTransaction(Account ownerAccount, int recipientAccId, BigDecimal amount) {
        transfer(ownerAccount, recipientAccId, amount);
    }


    private void income(Account ownerAccount, BigDecimal amount) {
        //Реализовал только обновление баланса в таблице аккаунтов. Надо еще реализовать создание транзакции и запись в таблицу транзакций
        BigDecimal updatedBalance = ownerAccount.getBalance().add(amount);
        ownerAccount.setBalance(updatedBalance);
        accountService.accountDao.update(ownerAccount);
    }

    private void expense(Account ownerAccount, BigDecimal amount) {
        //Реализовал только обновление баланса в таблице аккаунтов. Надо еще реализовать создание транзакции и запись в таблицу транзакций
        if(isPossible(ownerAccount,amount)){
            BigDecimal updatedBalance = ownerAccount.getBalance().subtract(amount);
            ownerAccount.setBalance(updatedBalance);
            accountService.accountDao.update(ownerAccount);
            System.out.println("Expense Transaction done");
        }else{
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private void transfer(Account ownerAccount, int recipientAccId, BigDecimal amount) {
        if(isPossible(ownerAccount,amount)){

        }else{
            System.out.println("Balance is lower than amount of transaction");
        }
    }

    private boolean isPossible(Account account, BigDecimal amount) {
        return (accountService.getBalance(account.getId()).compareTo(amount)) >= 0;
    }
    public BigDecimal readBigDecimal(Scanner scanner){
        while(true){
            try{
                String input = scanner.nextLine().trim();
                input = input.replace(',', '.');
                BigDecimal amount = new BigDecimal(input);
                if(amount.compareTo(BigDecimal.ZERO) <= 0){
                    System.out.println("Amount have to be greater than 0. Try again.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("number format exception :(");
            }
        }
    }

}
