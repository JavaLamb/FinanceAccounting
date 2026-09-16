package main.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.converters.AccountToAccountResponseConverter;
import main.dto.Request.CreateAccountRequest;
import main.dto.Response.AccountsResponse;
import main.entities.Account;
import main.exceptions.AccountException;
import main.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<AccountsResponse>> getAccounts(HttpServletRequest request) {
        long id = (long) request.getSession().getAttribute("id");
        List<AccountsResponse> list = accountService.getAllByUserId(id)
                .stream()
                .map(converter::convert)
                .toList();
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<AccountsResponse> createAccount(@RequestBody CreateAccountRequest dto, HttpServletRequest request){
        try{
            long id = (long) request.getSession().getAttribute("id");
            Account newAccount = accountService.createAccount(dto.getName(), id ,dto.getAccountType());
            URI url = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newAccount.getId())
                    .toUri();
            return created(url).body(converter.convert(newAccount));
        }catch (AccountException e){
            return status(HttpStatus.BAD_REQUEST).build();
        }
    }
    //надо сначала spring security подключить чтобы не делать двойной запрос к БД для проверки прав пользователя.
//    @GetMapping("/{id}")
//    public ResponseEntity<AccountsResponse> getAccountById(@PathVariable("id") long id, HttpServletRequest request){
//        accountService.findByIdService();
//    }
}
