package com.example.springrestdemo;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springrestdemo.controller.AuthController;
import com.example.springrestdemo.entity.PersonEntity;
import com.example.springrestdemo.entity.PersonRole;
import com.example.springrestdemo.repository.PersonRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @Test
    void login_ok() throws Exception {
        when(personRepository.findById(eq("u1")))
                .thenReturn(Optional.of(new PersonEntity("u1", "u1", "", PersonRole.user)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u1\",\"password\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.role").value("user"))
                .andExpect(jsonPath("$.name").value("u1"));
    }

    @Test
    void login_normalizesUsernameCase() throws Exception {
        when(personRepository.findById(eq("l2")))
                .thenReturn(Optional.of(new PersonEntity("l2", "L2", "", PersonRole.lead)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"L2\",\"password\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("l2"))
                .andExpect(jsonPath("$.role").value("lead"));
    }

    @Test
    void login_unknown_returns401() throws Exception {
        when(personRepository.findById(eq("ghost"))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ghost\",\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_validation() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
