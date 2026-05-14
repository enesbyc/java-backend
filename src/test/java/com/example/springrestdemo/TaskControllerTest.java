package com.example.springrestdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTasks_returnsList() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].id").value("t1"))
                .andExpect(jsonPath("$[0].assigneeId").value("p1"));
    }

    @Test
    void patchTask_updatesStatus() throws Exception {
        mockMvc.perform(patch("/api/tasks/t3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"in-progress\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t3"))
                .andExpect(jsonPath("$.status").value("in-progress"));
    }
}
