package com.flipcart.es.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipcart.es.entity.Customer;
import com.flipcart.es.entity.Seller;
import com.flipcart.es.entity.User;
import com.flipcart.es.enums.UserRole;
import com.flipcart.es.exceptions.EmailAlreadyVarifiedException;
import com.flipcart.es.exceptions.UserRoleNotFoundException;
import com.flipcart.es.repository.CustomerRepository;
import com.flipcart.es.repository.SellerRepository;
import com.flipcart.es.repository.UserRepository;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.service.AuthService;
import com.flipcart.es.utility.ResponseEntityProxy;
import com.flipcart.es.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService
{
	private UserRepository userRepository;
	private SellerRepository sellerRepository;
	private CustomerRepository customerRepository;
	private PasswordEncoder encoder;


	@SuppressWarnings("unchecked")
	private <T extends User> T mapToUserRequest(UserRequest userRequest)
	{
		User user = null;

		switch(UserRole.valueOf(userRequest.getUserRole().toUpperCase()))
		{
		case  SELLER -> {user = new Seller();}
		case CUSTOMER ->{user = new Customer();}
		default -> {throw new UserRoleNotFoundException("user with invalid user role");}
		}
		user.setUsername(userRequest.getEmail().split("@")[0].toString());
		user.setEmail(userRequest.getEmail());
		user.setPassword(encoder.encode(userRequest.getPassword()));
		user.setUserRole(UserRole.valueOf(userRequest.getUserRole().toUpperCase()));

		return (T) user;
	}

	private UserResponse mapToUserResponse(User user)
	{
		return UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> userRegister(UserRequest userRequest) 
	{
		User user = userRepository.findByUsername(userRequest.getEmail().split("@")[0].toString())
				.map(u->{
					if(u.isEmailVerified())
					{
						throw new  EmailAlreadyVarifiedException("user already existed with the specified email id");
					}
					else
					{
						//otp
					}
					return u;
				})
				.orElse(saveUser(userRequest));
		return ResponseEntityProxy.setResponseStructure(HttpStatus.ACCEPTED,"please varify through OTP sent on your mail id", mapToUserResponse(user));

	}

	public User saveUser(UserRequest userRequest)
	{
		User user=null;
		switch (UserRole.valueOf(userRequest.getUserRole().toUpperCase())) 
		{
		case SELLER ->
		{ 
			user=sellerRepository.save(mapToUserRequest(userRequest));
		}
		case CUSTOMER ->
		{
			user=customerRepository.save(mapToUserRequest(userRequest));
		}
		default ->{throw new UserRoleNotFoundException("user with invalid user role ");}
		}
		return user;
	}


}
