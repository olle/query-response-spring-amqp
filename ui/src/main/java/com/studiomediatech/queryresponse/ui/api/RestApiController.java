package com.studiomediatech.queryresponse.ui.api;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    private final RestApiAdapter adapter;

    public RestApiController(Optional<RestApiAdapter> maybe) {
        this.adapter = maybe.orElse(RestApiAdapter.empty());
    }

    @GetMapping("/api")
    public Map<String, Object> getApiRoot() {
        return Map.of("version", "v1");
    }

    @GetMapping("/api/v1")
    public Map<String, Object> none() {
        return Collections.emptyMap();
    }

    @GetMapping(path = "/api/v1", params = "q")
    public Map<String, Object> query(String q) {
        return Map.of("nope", "nop2");
    }
}
