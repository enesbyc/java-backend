package com.example.springrestdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPersons_returnsSeededUsersWithRoles() throws Exception {
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].id").value("l1"))
                .andExpect(jsonPath("$[0].name").value("l1"))
                .andExpect(jsonPath("$[0].role").value("lead"))
                .andExpect(jsonPath("$[4].id").value("u1"))
                .andExpect(jsonPath("$[4].role").value("user"))
                .andExpect(jsonPath("$[7].id").value("u4"))
                .andExpect(jsonPath("$[7].role").value("user"));
    }
}
