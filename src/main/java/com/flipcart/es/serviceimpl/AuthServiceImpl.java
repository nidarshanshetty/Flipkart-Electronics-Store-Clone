package com.flipcart.es.serviceimpl;

import java.util.EnumSet;

import org.springframework.stereotype.Service;

import com.flipcart.es.entity.User;
import com.flipcart.es.enums.UserRole;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService
{

	private User mapToUserRequest(UserRequest userRequest)
	{
		return User.builder()
				.email(userRequest.getEmail())
				.password(userRequest.getPassword())
				.userRole(UserRole.valueOf(userRequest.getUserRole()))
				.build();
	}

	private UserResponse mapToUserResponse(User user)
	{
		return UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.build();
	}


	@Override
	public void userRegister(UserRequest userRequest) 
	{
		UserRole userRole = UserRole.valueOf(userRequest.getUserRole().toUpperCase());

		if(EnumSet.allOf(UserRole.class).contains(userRole))
		{

		}
		else
		{

		}

	}

}
