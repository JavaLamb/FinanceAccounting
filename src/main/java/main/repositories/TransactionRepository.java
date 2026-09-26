package main.repositories;

import main.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
                SELECT t FROM Transaction t
                JOIN FETCH t.transactionCategory
                LEFT JOIN t.fromAccount fa
                LEFT JOIN t.toAccount ta
                WHERE (fa.id = :accountId OR ta.id = :accountId)
                AND (fa.user.id = :userId OR ta.user.id = :userId)
            """)
    List<Transaction> findAllByAccountIdAndUserIdWithCategory(@Param("accountId") long accountId, @Param("userId") long userId);
}
