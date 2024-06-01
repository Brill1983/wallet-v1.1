package ru.brill.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.user.dto.UserDto;
import ru.brill.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;
    UserDto userDto;

    @BeforeEach
    public void create() {
        userDto = new UserDto(null, "Иван", "Иванович", "ii@mail.ru");
    }

    @Test
    void createUser() {

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));
    }

    @Test
    void updateUser() {

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));

        UserDto userDtoForUpdate = new UserDto(null, "Петр", null, "pp@mail.ru");

        service.updateUser(userDtoForUpdate, 1L);

        query = em.createQuery("select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getFirstName(), equalTo(userDtoForUpdate.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getEmail(), equalTo((userDtoForUpdate.getEmail())));
    }

    @Test
    void updateUserWithWrongId() {
        UserDto userDtoForUpdate = new UserDto(null, "Петр", null, "pp@mail.ru");
        try {
            service.updateUser(userDtoForUpdate, 2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void getUserById() {

        service.createUser(userDto);

        UserDto user = service.getUserById(1L);

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));
    }

    @Test
    void getUserByIdWithWrongUserId() {
        try {
            service.getUserById(2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void deleteUser() {

        service.createUser(userDto);

        service.deleteUser(1L);

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        try {
            query.setParameter("id", 1L)
                    .getSingleResult();
        } catch (NoResultException thrown) {
            assertThat(thrown.getClass(), equalTo(NoResultException.class));
        }
    }

    @Test
    void deleteUserWithWrongId() {
        try {
            service.deleteUser(2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }
}
