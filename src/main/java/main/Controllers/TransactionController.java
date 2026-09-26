package main.Controllers;

import lombok.RequiredArgsConstructor;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Response.TransactionResponse;
import main.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@RequestMapping("/transactions")
@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionToTransactionResponseConverter converter;

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable("id") long accountId){
        List<TransactionResponse> transactionList = transactionService.findAllByAccId(accountId)
                .stream()
                .map(converter::convert)
                .toList();
        return ok(transactionList);
    }

}
