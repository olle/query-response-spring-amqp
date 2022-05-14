package com.studiomediatech.queryresponse.ui.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

	@GetMapping("/api")
	public Map<String, Object> getApiRoot() {
		return Map.of("version", "v1");
	}
}
