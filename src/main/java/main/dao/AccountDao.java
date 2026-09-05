package main.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import main.entities.Account;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Repository
public class AccountDao implements Dao<Account, Integer> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Account> findAll() {
        return List.of();
    }

    @Override
    public Optional<Account> findById(Integer id) {
        Account account = em.find(Account.class, id);
        return Optional.ofNullable(account);
    }

    @Override
    public Account insert(Account account) {
        em.persist(account);
        return account;
    }

    @Override
    public Account update(Account account) {
        return em.merge(account);
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    public List<Account> findAllByUsersId(Integer id) {
        return em.createNamedQuery("Account.findAllAccountsByUserId", Account.class)
                .setParameter("userId", id)
                .getResultList();
    }

    public Long countUsersAccounts(int id) {
        return em.createNamedQuery("Account.countAllAccountByUserId", Long.class)
                .setParameter("userId", id)
                .getSingleResult();
    }
}
