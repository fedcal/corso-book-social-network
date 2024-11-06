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
public class RegistrationRequest {

    @NotEmpty(message = "Nome utente obbligatorio")
    @NotBlank(message = "Nome utente obbligatorio")
    private String nome;

    @NotEmpty(message = "Cognome utente obbligatorio")
    @NotBlank(message = "Cognome utente obbligatorio")
    private String cognome;

    @NotEmpty(message = "Username utente obbligatorio")
    @NotBlank(message = "Username utente obbligatorio")
    private String username;

    @Email(message = "Email non corretta")
    @NotEmpty(message = "Email utente obbligatorio")
    @NotBlank(message = "Email utente obbligatorio")
    private String email;

    @NotEmpty(message = "Password utente obbligatorio")
    @NotBlank(message = "Password utente obbligatorio")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String password;
}
