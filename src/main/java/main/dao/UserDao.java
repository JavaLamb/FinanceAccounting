package main.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import main.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Repository
public class UserDao implements Dao<User, Integer> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(Integer id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public User insert(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    public Optional<User> findByEmail(String email) {
        try{
            User user = em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
