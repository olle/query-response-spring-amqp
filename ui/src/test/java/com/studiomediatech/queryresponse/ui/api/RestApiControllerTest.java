package com.studiomediatech.queryresponse.ui.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RestApiControllerTest {

    @Test
    void ensureHandlesQueryRequest() throws Exception {

        var mockMvc = MockMvcBuilders.standaloneSetup(new RestApiController(Optional.empty())).build();

        mockMvc.perform(get("/api/v1/").param("q", "hello")).andExpect(status().isOk());
    }

}
