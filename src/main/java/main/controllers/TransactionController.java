package main.controllers;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Request.CreateRegularTransactionRequest;
import main.dto.Request.CreateTransferRequest;
import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import main.exceptions.AccessNotAllowed;
import main.exceptions.BalanceException;
import main.exceptions.TransactionNotFound;
import main.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

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

    @PostMapping("/accounts/{accountId}")
    public ResponseEntity<TransactionResponse> createRegularTransaction(@PathVariable("accountId") long accountId,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @Validated @RequestBody CreateRegularTransactionRequest dto) {
        try {
            long userId = userDetails.getId();
            Transaction newTransaction = transactionService.createRegularTransaction(dto.transactionType(), dto.categoryId(), dto.amount(), userId, accountId);
            return ok(converter.convert(newTransaction));
        } catch (AccountNotFoundException e) {
            return status(HttpStatus.NOT_FOUND).build();
        } catch (AccessNotAllowed e) {
            return status(HttpStatus.FORBIDDEN).build();
        } catch (TransactionNotFound e) {
            return status(HttpStatus.CONFLICT).build();
        } catch (BalanceException e) {
            return status(HttpStatus.UNPROCESSABLE_CONTENT).build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("transactionId") long transactionId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long userId = userDetails.getId();
            Transaction transaction = transactionService.getTransactionById(userId, transactionId);
            return ok(converter.convert(transaction));
        } catch (AccessNotAllowed e) {
            return status(HttpStatus.NOT_FOUND).build();
        } catch (BalanceException e) {
            return status(HttpStatus.UNPROCESSABLE_CONTENT).build();
        }
    }

    @PostMapping("/accounts/{accountId}/transfer")
    public ResponseEntity<TransactionResponse> createTransfer(@PathVariable("accountId") long fromAccountId,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @Validated @RequestBody CreateTransferRequest dto) {
        try {
            long userId = userDetails.getId();
            Transaction newTransaction = transactionService.createTransfer(dto.toAccountId(), dto.amount(), dto.categoryId(), fromAccountId, userId);
            return ok(converter.convert(newTransaction));
        } catch (AccountNotFoundException e) {
            return status(HttpStatus.BAD_REQUEST).build();
        } catch (AccessNotAllowed e) {
            return status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> changeAmountOfTransaction(@PathVariable("transactionId") long transactionId,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @DecimalMax(value = "1000000.00", message = "Максимальный разовый перевод 1_000_000")
                                                                         @Digits(integer = 9, fraction = 2, message = "Некорректный формат суммы")
                                                                         @Positive @RequestParam BigDecimal newAmount) {
        long userId = userDetails.getId();
        transactionService.changeAmount(userId, transactionId, newAmount);
        return ok().build();
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> rollbackExistingTransaction(@PathVariable("transactionId") long transactionId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long userId = userDetails.getId();
            transactionService.deleteTransaction(transactionId, userId);
            return ok().build();
        } catch (BalanceException e) {
            return status(HttpStatus.UNPROCESSABLE_CONTENT).build();
        } catch (AccessNotAllowed e) {
            return status(HttpStatus.FORBIDDEN).build();
        }
    }

}
