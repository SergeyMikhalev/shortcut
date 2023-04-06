package ru.job4j.shortcut.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;
import ru.job4j.shortcut.service.ConvertService;


@AllArgsConstructor
@RestController
@RequestMapping("/convert")
public class ConvertController {
    private final ConvertService convertService;

    @PostMapping
    public ResponseEntity<ConvertResponse> convert(@RequestBody ConvertRequest request) {
        ConvertResponse response = convertService.convert(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> onRepositoryException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> onArgumentException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

}
