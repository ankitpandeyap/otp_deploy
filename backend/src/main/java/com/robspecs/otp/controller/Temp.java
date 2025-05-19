package com.robspecs.otp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/temp")
public class Temp {

	@GetMapping("/secure")
	public String secureEndpoint() {
	    return "This is a protected endpoint";
	}


}
