package com.flipcart.es.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.service.AuthService;

@RestController
public class AuthController 
{
	@Autowired
	private AuthService authService;
	
	@PostMapping("/users/register")
	public void userRegister(@RequestBody UserRequest userRequest)
	{
		authService.userRegister(userRequest);
	}
}
