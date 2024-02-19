package com.neu.cloud.cloudApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import com.neu.cloud.cloudApp.controller.UserController;
import com.neu.cloud.cloudApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateAndGetUser() throws Exception {
        // Create a user
        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test94@example.com\",\"password\":\"password\",\"first_name\":\"Test\",\"last_name\":\"User\"}"))
                .andExpect(status().isCreated());

        // Validate account exists
        mockMvc.perform(get("/v1/user/self")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("test94@example.com:password".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test94@example.com"));
    }

    @Test
    public void testUpdateAndGetUser() throws Exception {
        // Update the user
        mockMvc.perform(put("/v1/user/self")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("test@example.com:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first_name\":\"Updated\",\"last_name\":\"User\"}"))
                .andExpect(status().isNoContent());

        // Validate the account was updated
        mockMvc.perform(get("/v1/user/self")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("test@example.com:password".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Updated"))
                .andExpect(jsonPath("$.last_name").value("User"));
    }
}
