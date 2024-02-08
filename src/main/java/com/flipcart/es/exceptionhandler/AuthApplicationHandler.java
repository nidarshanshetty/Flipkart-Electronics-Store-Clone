package com.flipcart.es.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flipcart.es.exceptions.EmailAlreadyVarifiedException;
import com.flipcart.es.exceptions.InvalidOTPException;
import com.flipcart.es.exceptions.OTPExpiredException;
import com.flipcart.es.exceptions.RegistrationSessionExpiredException;
import com.flipcart.es.exceptions.UserRoleNotFoundException;

@RestControllerAdvice
public class AuthApplicationHandler 
{
	public ResponseEntity<Object>structure(HttpStatus status,String message,Object rootCause)
	{
		return new ResponseEntity<Object> (Map.of(
				"status",status.value(),
				"message",message,
				"rootCause",rootCause
				),status);
	}
	@ExceptionHandler(UserRoleNotFoundException.class)
	public ResponseEntity<Object>handleUserRoleNotFoundException(UserRoleNotFoundException ex)
	{
		return structure(HttpStatus.NOT_ACCEPTABLE,ex.getMessage(), "invalid userrole ");
	}

	@ExceptionHandler(EmailAlreadyVarifiedException.class)
	public ResponseEntity<Object>handleUserAlreadyVarifiedException(EmailAlreadyVarifiedException ex)
	{
		return structure(HttpStatus.ALREADY_REPORTED, ex.getMessage(),"email already varified");
	}

	@ExceptionHandler(OTPExpiredException.class)
	public ResponseEntity<Object>handleOTPExpiredException(OTPExpiredException ex)
	{
		return structure(HttpStatus.NOT_FOUND,ex.getMessage(), "OTP expired");
	}

	@ExceptionHandler(RegistrationSessionExpiredException.class)
	public ResponseEntity<Object>handleRegistrationSessionExpiredException(RegistrationSessionExpiredException ex)
	{
		return structure(HttpStatus.NOT_FOUND,ex.getMessage(),"Registration session expired");
	}
	@ExceptionHandler(InvalidOTPException.class)
	public ResponseEntity<Object>handleInvalidOTPException(InvalidOTPException ex)
	{
		return structure(HttpStatus.BAD_REQUEST,ex.getMessage(),"invalid otp");
	}
}
