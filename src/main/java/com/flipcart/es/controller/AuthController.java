package com.flipcart.es.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.service.AuthService;
import com.flipcart.es.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AuthController 
{

	private AuthService authService;

	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> userRegister(@RequestBody UserRequest userRequest)
	{
		return authService.userRegister(userRequest);
	}
	@PostMapping("/verify-otp")
	public ResponseEntity<String>verifyOTP(@RequestBody OtpModel otpModel)
	{
		return authService.verifyOTP(otpModel);
	}

}
