package main.Controllers;

import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.AccountToAccountResponseConverter;
import main.dto.Request.CreateAccountRequest;
import main.dto.Response.AccountsResponse;
import main.entities.Account;
import main.exceptions.AccountException;
import main.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.security.auth.login.AccountNotFoundException;
import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountToAccountResponseConverter converter;

    @GetMapping
    public ResponseEntity<List<AccountsResponse>> getAccounts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        long userId = userDetails.getId();
        List<AccountsResponse> list = accountService.getAllByUserId(userId)
                .stream()
                .map(converter::convert)
                .toList();
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<AccountsResponse> createAccount(@RequestBody CreateAccountRequest dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long userId = userDetails.getId();
            Account newAccount = accountService.createAccount(dto.getName(), userId, dto.getAccountType());
            URI url = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newAccount.getId())
                    .toUri();
            return created(url).body(converter.convert(newAccount));
        } catch (AccountException e) {
            return status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountsResponse> getAccountById(@PathVariable("id") long accountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Account account = accountService.findByIdService(accountId, userDetails.getId());
            return ok(converter.convert(account));
        } catch (AccountNotFoundException e) {
            return status(HttpStatus.NOT_FOUND).build();
        } catch (AccountException e) {
            return status(HttpStatus.FORBIDDEN).build();
        }
    }
}
