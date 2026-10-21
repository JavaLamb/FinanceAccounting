package main.repositories;

import main.entities.*;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            "postgres:16-alpine")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Category category;
    private Account fromAccount;
    private Account toAccount;
    private Transaction mainTransaction;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testuser@example.com", "hashed_password"));
        category = categoryRepository.save(new Category("Shopping", testUser));

        fromAccount = new Account("Source Account", testUser, AccountType.DEBIT);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount = accountRepository.save(fromAccount);

        toAccount = new Account("Destination Account", testUser, AccountType.CREDIT);
        toAccount.setBalance(new BigDecimal("500.00"));
        toAccount = accountRepository.save(toAccount);

        mainTransaction = new Transaction(
                TransactionType.TRANSFER, fromAccount, toAccount, category, new BigDecimal("100.00")
        );
        mainTransaction = transactionRepository.save(mainTransaction);

        Account foreignAccount = new Account("Foreign Account", testUser, AccountType.DEBIT);
        foreignAccount = accountRepository.save(foreignAccount);

        Transaction foreignTransaction = new Transaction(
                TransactionType.EXPENSE, foreignAccount, null, category, new BigDecimal("50.00")
        );
        transactionRepository.save(foreignTransaction);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldReturnTransactionWithFetchedAccounts() {
        Optional<Transaction> result = transactionRepository.findByIdWithAccounts(mainTransaction.getId());

        assertThat(result).isPresent();
        Transaction fetched = result.get();

        assertThat(Hibernate.isInitialized(fetched.getFromAccount())).isTrue();
        assertThat(Hibernate.isInitialized(fetched.getToAccount())).isTrue();
        assertThat(Hibernate.isInitialized(fetched.getCategory())).isFalse();

        assertThat(fetched.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(fetched.getCategory().getId()).isEqualTo(category.getId());
        assertThat(fetched.getFromAccount().getName()).isEqualTo("Source Account");
    }

    @Test
    void shouldReturnOnlyTargetTransactionWithFetchedCategory() {
        List<Transaction> results = transactionRepository.findAllByAccountIdWithCategory(fromAccount.getId());

        assertThat(results).hasSize(1);

        Transaction fetched = results.getFirst();
        assertThat(fetched.getId()).isEqualTo(mainTransaction.getId());

        assertThat(Hibernate.isInitialized(fetched.getCategory())).isTrue();
        assertThat(Hibernate.isInitialized(fetched.getFromAccount())).isFalse();
        assertThat(Hibernate.isInitialized(fetched.getToAccount())).isFalse();

        assertThat(fetched.getCategory().getName()).isEqualTo("Shopping");
    }

    @Test
    void shouldReturnEmptyListWhenAccountHasNoTransactions() {
        List<Transaction> results = transactionRepository.findAllByAccountIdWithCategory(999L);

        assertThat(results).isEmpty();
    }
}
