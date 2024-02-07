package com.flipcart.es.utility;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ResponseStructure <T>
{
	private Integer status;
	private String message;
	private T data;
}
