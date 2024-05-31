package ru.brill.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.brill.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    public void itemCreate() {
        userDto = new UserDto("Иван", "Иванович", "ii@mail.ru");
        response = new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Test
    void saveUserTest() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(response);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .createUser(any());
    }

    @Test
    void saveUserFirstNameIsNull() throws Exception {
        userDto.setFirstName(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserFirstNameIsMixedLanguage() throws Exception {
        userDto.setFirstName("Иваfre");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserFirstNameIsTooLong() throws Exception {
        userDto.setFirstName("ИвановИвановИвановИвановИвановИвановИвановИвановИванов");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserFirstNameIsWhiteSpace() throws Exception {
        userDto.setFirstName(" ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserFirstNameIsBlank() throws Exception {
        userDto.setFirstName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserLastNameIsNull() throws Exception {
        userDto.setLastName(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserLastNameIsMixedLanguage() throws Exception {
        userDto.setLastName("Иваfre");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserLastNameIsTooLong() throws Exception {
        userDto.setLastName("ИвановИвановИвановИвановИвановИвановИвановИвановИванов");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserLastNameIsWhiteSpace() throws Exception {
        userDto.setLastName(" ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserLastNameIsBlank() throws Exception {
        userDto.setLastName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserWrongEmail() throws Exception {
        userDto.setEmail("ssss.su");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserEmailIsNull() throws Exception {
        userDto.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserEmailIsTooLong() throws Exception {
        userDto.setEmail("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii@mail.ru");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void updateUser() throws Exception {
        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserWrongEmail() throws Exception {
        userDto.setEmail("aaaaa.ru");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserNullEmail() throws Exception {
        userDto.setEmail(null);

        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserNullFirstName() throws Exception {
        userDto.setFirstName(null);

        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserNullLastName() throws Exception {
        userDto.setLastName(null);

        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserMixedLanguageFirstName() throws Exception {
        userDto.setFirstName("Bdfyнов");

        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserMixedLanguageLastNameName() throws Exception {
        userDto.setLastName("Bdfyнов");

        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .updateUser(any(), anyLong());
    }

    @Test
    void getUserById() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(response);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .getUserById(anyLong());
    }

    @Test
    void deleteUserById() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1))
                .deleteUser(userId);
    }
}
