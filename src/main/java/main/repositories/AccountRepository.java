package main.repositories;

import main.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndActiveTrue(long userId);
    int countAllByUserIdAndActiveTrue(long userid);
    @Modifying
    @Query("""
                UPDATE Account a
                SET a.active = false
                WHERE a.id = :accountId
                    AND a.user.id = :userId
                    AND a.active = true
            """)
    int deactivateByIdAndUserId(@Param("accountId") long accountId,@Param("userId") long userId);
    Optional<Account> findByIdAndActiveTrue(long accountId);
}
