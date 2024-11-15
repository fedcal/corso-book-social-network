package com.federico.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email(message = "Email non corretta")
    @NotEmpty(message = "Email utente obbligatorio")
    @NotBlank(message = "Email utente obbligatorio")
    private String email;

    @NotEmpty(message = "Password utente obbligatorio")
    @NotBlank(message = "Password utente obbligatorio")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String password;
}
