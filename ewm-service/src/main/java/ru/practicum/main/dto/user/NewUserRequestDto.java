package ru.practicum.main.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequestDto {

    @NotBlank(message = "Name can't be blank")
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank(message = "Email can't be blank")
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
