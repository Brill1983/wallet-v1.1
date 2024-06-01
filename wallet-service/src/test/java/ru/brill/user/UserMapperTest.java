package ru.brill.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.brill.user.dto.UserDto;
import ru.brill.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private User user;
    public UserDto userDto;

    @BeforeEach
    public void create() {
        user = new User(1L, "Иван", "Иванович", "ii@Mail.ru");
        userDto = new UserDto(1L, "Иван", "Иванович", "ii@mail.ru");
    }

    @Test
    void toUserDto() {
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUser() {
        User user = UserMapper.toUser(userDto);

        assertEquals(1L, user.getId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void userDtoToUserWithUser() {
        userDto.setEmail(null);

        User user2 = new User(1L, "Петр", "Петрович", "pp@mail.ru");

        User mappedUser = UserMapper.toUser(userDto, user2);

        assertEquals(1L, mappedUser.getId());
        assertEquals("Иван", mappedUser.getFirstName());
        assertEquals("Иванович", mappedUser.getLastName());
        assertEquals("pp@mail.ru", mappedUser.getEmail());
    }
}
