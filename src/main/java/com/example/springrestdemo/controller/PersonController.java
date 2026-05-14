package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.PersonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Person", description = "İsim ve soyisim API")
public class PersonController {

    @GetMapping("/persons")
    @Operation(summary = "Örnek kişi listesi", description = "Birden fazla name ve surname içeren JSON dizi döner.")
    public List<PersonResponse> getPersons() {
        return List.of(
                new PersonResponse("p1", "Ahmet", "Yılmaz"),
                new PersonResponse("p2", "Ayşe", "Kaya"),
                new PersonResponse("p3", "Mehmet", "Demir"));
    }
}
