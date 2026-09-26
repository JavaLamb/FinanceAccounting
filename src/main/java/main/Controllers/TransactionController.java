package main.Controllers;

import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.TransactionToTransactionResponseConverter;
import main.dto.Response.TransactionResponse;
import main.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
