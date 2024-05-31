package ru.brill.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.brill.service.Create;
import ru.brill.service.Update;
import ru.brill.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("В метод saveUser передан userDto {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserDto userDto, @PathVariable Long userId) {
        log.info("В метод updateUser передан userId {}, userDto {}", userId, userDto);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("В метод getUserById передан userId {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("В метод deleteUser передан userId {}", userId);
        userClient.deleteUser(userId);
    }
}
