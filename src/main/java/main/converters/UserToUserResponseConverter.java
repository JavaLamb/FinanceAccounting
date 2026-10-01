package main.converters;

import main.entities.User;
import main.dto.Response.UserResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserResponseConverter implements Converter<User, UserResponse> {

    @Override
    public UserResponse convert(User source) {
        return UserResponse.builder()
                .id(source.getId())
                .email(source.getEmail())
                .build();
    }
}
