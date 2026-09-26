package main.converters;

import main.dto.Response.AccountsResponse;
import main.entities.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountToAccountResponseConverter implements Converter<Account, AccountsResponse> {
    @Override
    public AccountsResponse convert(Account source) {
        return new AccountsResponse(source.getId(), source.getName(), source.getBalance(),source.getAccountType());
    }
}
