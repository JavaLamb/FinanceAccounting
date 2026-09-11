package main.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.converters.AccountToAccountResponseConverter;
import main.dto.Response.AccountsResponse;
import main.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountToAccountResponseConverter converter;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountsResponse>> getAccounts(HttpServletRequest request) {
        long id = (long) request.getSession().getAttribute("id");
        List<AccountsResponse> list = accountService.getAllByUserId(id)
                .stream()
                .map(converter::convert)
                .toList();
        return ok(list);
    }
}
