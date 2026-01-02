package com.cqu.exp04.controller;

import com.cqu.exp04.entity.Course;
import com.cqu.exp04.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void createCourse_returnsSuccessResult() throws Exception {
        Course created = Course.builder()
                .courseNo("CS101")
                .courseName("计算机导论")
                .credit(new BigDecimal("3.0"))
                .hours(48)
                .courseType("必修")
                .description("intro")
                .build();

        Mockito.when(adminService.createCourse(Mockito.any()))
                .thenReturn(created);

        String body = "{\"courseNo\":\"CS101\",\"courseName\":\"计算机导论\",\"credit\":3.0,\"hours\":48,\"courseType\":\"必修\",\"description\":\"intro\"}";

        mockMvc.perform(post("/api/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
