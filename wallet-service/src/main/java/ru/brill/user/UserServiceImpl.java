package ru.brill.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.service.ValidationService;
import ru.brill.user.dao.UserRepository;
import ru.brill.user.dto.UserDto;
import ru.brill.user.model.User;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ValidationService validator;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userFromRepos = userRepository.save(user);
        return UserMapper.toUserDto(userFromRepos);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        User userFromDto = UserMapper.toUser(userDto, user);
        userFromDto.setId(userId);
        return UserMapper.toUserDto(userRepository.save(userFromDto));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        validator.validUserId(userId);
        userRepository.deleteById(userId);
    }
}
