package com.flipcart.es.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoggedInException  extends RuntimeException
{
private String message;
}
