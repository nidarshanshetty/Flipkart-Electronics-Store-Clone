package com.flipcart.es.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidOTPException extends RuntimeException
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String message;
}
