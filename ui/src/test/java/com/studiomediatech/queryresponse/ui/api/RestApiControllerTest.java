package com.studiomediatech.queryresponse.ui.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RestApiControllerTest {

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RestApiControllerPort(Optional.empty())).build();
    }

    @Test
    void ensureHandlesQueryRequest() throws Exception {
        mockMvc.perform(get("/api/v1/").param("q", "hello")).andExpect(status().isOk());
    }

    @Test
    void ensureHandlesNodesRequest() throws Exception {
        mockMvc.perform(get("/api/v0/nodes")).andExpect(status().isOk());
    }

}
