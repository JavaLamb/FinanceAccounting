package main.controllers;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Request.CreateRegularTransactionRequest;
import main.dto.Request.CreateTransferRequest;
import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import main.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Validated
@RequestMapping("/transactions")
@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionToTransactionResponseConverter converter;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable("accountId") long accountId,
                                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        long userId = userDetails.getId();
        List<TransactionResponse> transactionList = transactionService.findAllByAccId(accountId, userId)
                .stream()
                .map(converter::convert)
                .toList();
        return ok(transactionList);
    }

    @PostMapping("/accounts/{accountId}")
    public ResponseEntity<TransactionResponse> createRegularTransaction(@PathVariable("accountId") long accountId,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @Validated @RequestBody CreateRegularTransactionRequest dto) {
        long userId = userDetails.getId();
        Transaction newTransaction = transactionService.createRegularTransaction(dto.transactionType(), dto.categoryId(), dto.amount(), userId, accountId);
        return ok(converter.convert(newTransaction));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("transactionId") long transactionId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        long userId = userDetails.getId();
        Transaction transaction = transactionService.getTransactionById(userId, transactionId);
        return ok(converter.convert(transaction));

    }

    @PostMapping("/accounts/{accountId}/transfer")
    public ResponseEntity<TransactionResponse> createTransfer(@PathVariable("accountId") long fromAccountId,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @Validated @RequestBody CreateTransferRequest dto) {
        long userId = userDetails.getId();
        Transaction newTransaction = transactionService.createTransfer(dto.toAccountId(), dto.amount(), dto.categoryId(), fromAccountId, userId);
        return ok(converter.convert(newTransaction));
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> changeAmountOfTransaction(@PathVariable("transactionId") long transactionId,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @Validated @RequestBody BigDecimal newAmount) {
        long userId = userDetails.getId();
        Transaction newTransaction = transactionService.changeAmount(userId, transactionId, newAmount);
        return ok().body(converter.convert(newTransaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> rollbackExistingTransaction(@PathVariable("transactionId") long transactionId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long userId = userDetails.getId();
        transactionService.deleteTransaction(transactionId, userId);
        return ok().build();

    }

}
