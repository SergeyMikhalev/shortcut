package ru.job4j.shortcut.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.shortcut.service.RedirectionService;

@Controller
@AllArgsConstructor
public class RedirectionController {

    private final RedirectionService redirectionService;

    @GetMapping("/redirect/{code}")
    public ResponseEntity<String> redirect(@PathVariable String code) {
        return new ResponseEntity<>(redirectionService.getRedirectionURL(code), HttpStatus.MOVED_TEMPORARILY);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> onArgumentException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

}
