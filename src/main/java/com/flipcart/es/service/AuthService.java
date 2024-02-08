package com.flipcart.es.service;

import org.springframework.http.ResponseEntity;

import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.utility.ResponseStructure;

public interface AuthService
{
	ResponseEntity<ResponseStructure<UserResponse>> userRegister(UserRequest userRequest);

	ResponseEntity<String> verifyOTP(OtpModel otpModel);
}
