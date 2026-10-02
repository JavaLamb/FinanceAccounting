package main.controllers;

import lombok.RequiredArgsConstructor;
import main.config.CustomUserDetails;
import main.converters.AccountToAccountResponseConverter;
import main.dto.Request.CreateAccountRequest;
import main.dto.Response.AccountsResponse;
import main.entities.Account;
import main.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<AccountsResponse> createAccount(@Validated @RequestBody CreateAccountRequest dto,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        long userId = userDetails.getId();
        Account newAccount = accountService.createAccount(dto.name(), userId, dto.accountType());
        URI url = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAccount.getId())
                .toUri();
        return created(url).body(converter.convert(newAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable("id") long accountId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        accountService.deactivateAccount(accountId, userDetails.getId());
        return ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountsResponse> getAccountById(@PathVariable("id") long accountId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Account account = accountService.findByIdService(accountId, userDetails.getId());
        return ok(converter.convert(account));
    }
}
