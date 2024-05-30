package ru.brill.user;

import lombok.experimental.UtilityClass;
import ru.brill.user.dto.UserDto;
import ru.brill.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail()
        );
    }

    public static User toUser(UserDto userDto, User user) {
        return new User(
                user.getId(),
                userDto.getFirstName() != null ? userDto.getFirstName() : user.getFirstName(),
                userDto.getLastName() != null ? userDto.getLastName() : user.getLastName(),
                userDto.getEmail() != null ? userDto.getEmail() : user.getEmail()
        );
    }
}
