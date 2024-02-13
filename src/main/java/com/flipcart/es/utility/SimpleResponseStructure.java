package com.flipcart.es.utility;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class SimpleResponseStructure <T>
{
	private Integer status;
	private String message;
}
