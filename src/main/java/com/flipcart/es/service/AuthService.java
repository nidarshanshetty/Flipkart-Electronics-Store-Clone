package com.flipcart.es.service;

import org.springframework.http.ResponseEntity;

import com.flipcart.es.requestdto.AuthRequest;
import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.AuthResponse;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.utility.ResponseStructure;
import com.flipcart.es.utility.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService
{
	ResponseEntity<ResponseStructure<UserResponse>> userRegister(UserRequest userRequest);

	ResponseEntity<String> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse response,String accessToken,String refreshToken);

	ResponseEntity<SimpleResponseStructure<String>> logout(String refreshToken,String accessToken, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure<String>> revokeAllDevice(HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure<String>> revokeOtherDevice(String refreshToken, String accessToken,
			HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure<String>> refreshLoginandTokenRotation( String accessToken,String refreshToken,
			HttpServletResponse response);
}
