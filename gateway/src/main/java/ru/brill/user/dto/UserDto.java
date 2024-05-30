package ru.brill.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.brill.service.Create;
import ru.brill.service.NameConstraint;
import ru.brill.service.Update;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull(groups = {Create.class}, message = "Имя пользователя обязательно к заполнению и не может быть пустым")
    @NameConstraint
    private String firstName;

    @NotNull(groups = {Create.class}, message = "Фамилия пользователя обязательна к заполнению и не может быть пустой")
    @NameConstraint
    private String lastName;

    @NotBlank(groups = {Create.class}, message = "Электронная почта не может быть пустой")
    @Email(groups = {Create.class, Update.class}, message = "Передан неправильный формат email")
    @Size(max = 50)
    private String email;
}
