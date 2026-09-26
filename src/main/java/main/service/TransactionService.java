package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Transaction;
import main.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<Transaction> findAllByAccId(long accountId){
        return transactionRepository.findAllByAccountIdWithCategory(accountId);
    }
}
