package com.studiomediatech.queryresponse.ui.api;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Map<String, Object> query(String q, // NOSONAR
            @RequestParam(name = "timeout", required = false, defaultValue = "0") int timeout,
            @RequestParam(name = "t", required = false, defaultValue = "0") int t,
            @RequestParam(name = "limit", required = false, defaultValue = "0") int limit,
            @RequestParam(name = "l", required = false, defaultValue = "0") int l) {
        return adapter.query(q, Math.max(0, Math.max(timeout, t)), Math.max(0, Math.max(limit, l)));
    }

    @GetMapping("/api/v1/nodes")
    public Map<String, Object> nodes() {
        return adapter.nodes();
    }
}
