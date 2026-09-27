package main.controllers;

import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Request.CreateRegularTransactionRequest;
import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import main.exceptions.AccessNotAllowed;
import main.exceptions.BalanceException;
import main.exceptions.TransactionNotFound;
import main.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;


@RequestMapping("/transactions")
@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionToTransactionResponseConverter converter;

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable("id") long accountId, @AuthenticationPrincipal CustomUserDetails userDetails){
        try {
            long userId = userDetails.getId();
            List<TransactionResponse> transactionList = transactionService.findAllByAccId(accountId, userId)
                    .stream()
                    .map(converter::convert)
                    .toList();
            return ok(transactionList);
        } catch (Exception e) {
            return status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/accounts/{id}")
    public ResponseEntity<TransactionResponse> createRegularTransaction(@PathVariable("id") long accountId, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateRegularTransactionRequest dto){
        try {
            long userId = userDetails.getId();
            Transaction newTransaction = transactionService.createRegularTransaction(dto.getTransactionType(),dto.getCategoryId(),dto.getAmount(), userId, accountId);
            return ok(converter.convert(newTransaction));
        } catch (AccountNotFoundException e) {
            return status(HttpStatus.NOT_FOUND).build();
        } catch (AccessNotAllowed e){
            return status(HttpStatus.FORBIDDEN).build();
        } catch (TransactionNotFound e){
            return status(HttpStatus.CONFLICT).build();
        } catch (BalanceException e){
            return status(HttpStatus.UNPROCESSABLE_CONTENT).build();
        }
    }

//    @PostMapping("/accounts/{id}/transfer")
//    public ResponseEntity<TransactionResponse> createTransferTransaction(@PathVariable("id") long accountId, @AuthenticationPrincipal CustomUserDetails userDetails){
//
//    }

}
