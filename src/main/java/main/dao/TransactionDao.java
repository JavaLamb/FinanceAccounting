package main.dao;

import main.entities.Transaction;

import java.util.List;

public class TransactionDao implements Dao<Transaction,Integer>{
    @Override
    public List<Transaction> findAll() {
        return List.of();
    }

    @Override
    public Transaction findById(Integer integer) {
        return null;
    }

    @Override
    public Transaction insert(Transaction transaction) {
        return null;
    }

    @Override
    public Transaction update(Transaction transaction) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }
}
