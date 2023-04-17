package ru.job4j.shortcut.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.service.RegistrationService;

@RestController
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {
        return new ResponseEntity<>(registrationService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/wrong")
    public ResponseEntity<String> registerWrong(@RequestBody RegistrationRequest request) {
        registrationService.some(request.getSite());
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> onRepositoryException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
