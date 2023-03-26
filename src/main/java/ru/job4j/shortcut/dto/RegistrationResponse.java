package ru.job4j.shortcut.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private boolean registration;
    private String login;
    private String password;
}
