package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.LoginRequest;
import com.example.springrestdemo.dto.PersonResponse;
import com.example.springrestdemo.repository.PersonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth", description = "DB kullanıcıları ile giriş")
public class AuthController {

    private final PersonRepository personRepository;

    public AuthController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Giriş", description = "Kullanıcı adı kişi id'si ile eşleşir (ör. u1, l2). Rol DB'den gelir.")
    public ResponseEntity<PersonResponse> login(@Valid @RequestBody LoginRequest request) {
        String key = request.username().trim().toLowerCase();
        return personRepository
                .findById(key)
                .map(p -> ResponseEntity.ok(new PersonResponse(
                        p.getId(), p.getName(), p.getSurname(), p.getRole().name())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
