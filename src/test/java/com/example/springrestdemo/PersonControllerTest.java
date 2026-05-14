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
    void getPersons_returnsListOfNameAndSurname() throws Exception {
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[0].name").value("Ahmet"))
                .andExpect(jsonPath("$[0].surname").value("Yılmaz"))
                .andExpect(jsonPath("$[1].id").value("p2"))
                .andExpect(jsonPath("$[1].name").value("Ayşe"))
                .andExpect(jsonPath("$[1].surname").value("Kaya"))
                .andExpect(jsonPath("$[2].id").value("p3"))
                .andExpect(jsonPath("$[2].name").value("Mehmet"))
                .andExpect(jsonPath("$[2].surname").value("Demir"));
    }
}
