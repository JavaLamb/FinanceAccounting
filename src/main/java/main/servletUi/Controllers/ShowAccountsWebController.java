package main.servletUi.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import main.entities.Account;
import main.service.AccountService;
import main.servletUi.core.WebController;
import main.servletUi.dto.ApiResponse;
import main.servletUi.dto.Request.EmptyRequest;
import main.servletUi.dto.Response.ShowAccountsResponse;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller("/accounts")
public class ShowAccountsWebController implements WebController<EmptyRequest, List<ShowAccountsResponse>> {
    private final AccountService accountService;

    @Override
    public ApiResponse<List<ShowAccountsResponse>> execute(EmptyRequest emptyRequest, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        List<Account> accountsList = accountService.findAllByUserId(userId);
        List<ShowAccountsResponse> resList = new ArrayList<>();
        for (Account a : accountsList) {
            resList.add(new ShowAccountsResponse(a.getId(), a.getName(), a.getBalance(), a.getAccountType()));
        }
        return new ApiResponse<>(200, resList);
    }

    @Override
    public Class<EmptyRequest> getRequestClass() {
        return EmptyRequest.class;
    }
}