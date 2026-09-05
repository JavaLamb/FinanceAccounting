package main.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import main.entities.Transaction;
import main.entities.TransactionCategory;
import main.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Repository
public class TransactionDao implements Dao<Transaction, Integer> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Transaction> findAll() {
        return List.of();
    }

    @Override
    public Optional<Transaction> findById(Integer id) {
        Transaction transaction = em.find(Transaction.class, id);
        return Optional.ofNullable(transaction);
    }

    @Override
    public Transaction insert(Transaction transaction) {
        em.persist(transaction);
        return transaction;
    }

    public TransactionCategory insertCategory(TransactionCategory category) {
        em.persist(category);
        return category;
    }

    public List<TransactionCategory> findAllCategoriesByUser(User user) {
        return em.createNamedQuery("TransactionCategory.findAllCategoriesByUser", TransactionCategory.class)
                .setParameter("user", user)
                .getResultList();
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
