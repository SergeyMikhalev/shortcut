package ru.job4j.shortcut.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;

@RestController
@RequestMapping("/convert")
public class ConvertController {

    @PostMapping
    public ResponseEntity<ConvertResponse> convert(@RequestBody ConvertRequest request,
                                                   @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        System.out.println(principal);
        return new ResponseEntity<>(new ConvertResponse("Authenticated! " +
                principal + " " + request.getUrl()), HttpStatus.CREATED);
    }
}
