package main.repositories;

import main.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndActiveTrue(long userId);

    Optional<Account> findByIdAndUserIdAndActiveTrue(long accountId, long userId);

    int countAllByUserIdAndActiveTrue(long userid);

    Optional<Account> findByIdAndActiveTrue(long accountId);
}
