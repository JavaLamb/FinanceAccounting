package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Transaction;
import main.exceptions.TransactionNotFound;
import main.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<Transaction> findAllByAccId(long accountId, long userId){
        List<Transaction> list = transactionRepository.findAllByAccountIdAndUserIdWithCategory(accountId, userId);
        if(list.isEmpty()){
            throw new TransactionNotFound("not found");
        }
        return list;
    }
}
