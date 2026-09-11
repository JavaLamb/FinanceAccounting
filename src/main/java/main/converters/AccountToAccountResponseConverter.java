package main.converters;

import main.dto.Response.AccountsResponse;
import main.entities.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountToAccountResponseConverter implements Converter<Account, AccountsResponse> {
    @Override
    public AccountsResponse convert(Account account) {
        return new AccountsResponse(account.getId(), account.getName(), account.getBalance(),account.getAccountType());
    }
}
