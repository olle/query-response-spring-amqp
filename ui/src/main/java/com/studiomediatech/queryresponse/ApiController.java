package com.studiomediatech.queryresponse;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@GetMapping("/api")
	public Map<String, Object> getApiRoot() {

		return Map.of("version", "v1");
	}

}
