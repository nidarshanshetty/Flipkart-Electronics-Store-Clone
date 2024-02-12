package com.flipcart.es.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipcart.es.requestdto.AuthRequest;
import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.AuthResponse;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.service.AuthService;
import com.flipcart.es.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
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

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>>login(@RequestBody AuthRequest authRequest,HttpServletResponse response)
	{
		return authService.login(authRequest,response);
	}

	@PreAuthorize("hasAuthority('SELLER')OR hasAuthority('CUSTOMER')")
	@PutMapping("/logout")
	public ResponseEntity<ResponseStructure<String>>logout(@CookieValue(name="rt" ,required = false)String refreshToken,
			@CookieValue(name="at",required = false)String accessToken,HttpServletResponse response)
	{
		return authService.logout(refreshToken,accessToken,response);
	}

}
