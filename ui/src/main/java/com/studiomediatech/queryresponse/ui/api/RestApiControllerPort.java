package com.studiomediatech.queryresponse.ui.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studiomediatech.queryresponse.ui.app.adapter.RestApiAdapter;

@RestController
public class RestApiControllerPort {

    private final RestApiAdapter adapter;

    public RestApiControllerPort(Optional<RestApiAdapter> maybe) {
        this.adapter = maybe.orElse(RestApiAdapter.empty());
    }

    @GetMapping("/api")
    public Map<String, Object> showApi() {
        return Response.from(Map.of("now", Instant.now())).withLinks("v0", "/api/v0", "v1", "/api/v1");
    }

    @GetMapping("/api/v0")
    public Map<String, Object> v0() {
        return Response.from(Map.of("version", "v0", "now", Instant.now())).withLinks("nodes", "/api/v0/nodes");
    }

    @GetMapping("/api/v0/nodes")
    public Map<String, Object> nodes() {
        return adapter.nodes();
    }

    @GetMapping("/api/v1")
    public Map<String, Object> v1() {
        return Response.from(Map.of("version", "v1", "now", Instant.now())).withLinks("query-response",
                "/api/v1?q=query");
    }

    @GetMapping(path = "/api/v1", params = "q")
    public Map<String, Object> query(String q, // NOSONAR
            @RequestParam(name = "timeout", required = false, defaultValue = "0") int timeout,
            @RequestParam(name = "t", required = false, defaultValue = "0") int t,
            @RequestParam(name = "limit", required = false, defaultValue = "0") int limit,
            @RequestParam(name = "l", required = false, defaultValue = "0") int l) {

        int normalizedTimeout = Math.max(0, Math.max(timeout, t));
        int normalizedLimit = Math.max(0, Math.max(limit, l));

        return adapter.query(q, normalizedTimeout, normalizedLimit);
    }

    protected interface Response {
        public static ResponseBuilder from(Map<String, Object> map) {
            return new ResponseBuilder(map);
        }
    }

    static class ResponseBuilder {

        private final Map<String, Object> map;

        private ResponseBuilder(Map<String, Object> map) {
            this.map = map;
        }

        public Map<String, Object> withLinks(String... args) {

            var links = new ArrayList<Link>();

            for (int i = 0; i < args.length; i = i + 2) {
                var rel = args[i];
                var val = args[i + 1];
                links.add(new Link(rel, val));
            }

            var results = new LinkedHashMap<>(map);
            results.put("_links", links);
            return results;
        }
    }

    protected record Link(String rel, String href) {
        // OK
    }

}
