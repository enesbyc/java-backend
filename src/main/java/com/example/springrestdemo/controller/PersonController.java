package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.PersonResponse;
import com.example.springrestdemo.repository.PersonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Person", description = "İsim ve soyisim API")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/persons")
    @Operation(summary = "Kişi listesi", description = "H2 veritabanından okur.")
    @Transactional(readOnly = true)
    public List<PersonResponse> getPersons() {
        return personRepository.findAllByOrderByIdAsc().stream()
                .map(e -> new PersonResponse(
                        e.getId(), e.getName(), e.getSurname(), e.getRole().name()))
                .toList();
    }
}
