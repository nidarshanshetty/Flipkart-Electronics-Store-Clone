package com.flipcart.es.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityProxy 
{
	public static <T> ResponseEntity<ResponseStructure<T>> setResponseStructure(HttpStatus status,String message,T data)
	{
		ResponseStructure<T> responseStructure = new ResponseStructure<T>();
		responseStructure.setStatus(status.value());
		responseStructure.setMessage(message);
		responseStructure.setData(data);

		return new  ResponseEntity<ResponseStructure<T>>(responseStructure,status);

	}

	public static <T>ResponseEntity<SimpleResponseStructure<T>>setSimpleResponseStructure(HttpStatus status,String message)
	{
		SimpleResponseStructure<T>responseStructure= new SimpleResponseStructure<T>();
		responseStructure.setStatus(status.value());
		responseStructure.setMessage(message);

		return new  ResponseEntity<SimpleResponseStructure<T>>(responseStructure,status);
	}
}
