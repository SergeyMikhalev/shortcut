package ru.job4j.shortcut.service;

import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse register(RegistrationRequest request);

    void some(String site);
}
