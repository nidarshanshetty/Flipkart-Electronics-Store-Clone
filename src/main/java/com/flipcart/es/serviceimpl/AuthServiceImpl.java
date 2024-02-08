package com.flipcart.es.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipcart.es.cache.CacheStore;
import com.flipcart.es.entity.Customer;
import com.flipcart.es.entity.Seller;
import com.flipcart.es.entity.User;
import com.flipcart.es.enums.UserRole;
import com.flipcart.es.exceptions.EmailAlreadyVarifiedException;
import com.flipcart.es.exceptions.InvalidOTPException;
import com.flipcart.es.exceptions.OTPExpiredException;
import com.flipcart.es.exceptions.RegistrationSessionExpiredException;
import com.flipcart.es.exceptions.UserRoleNotFoundException;
import com.flipcart.es.repository.CustomerRepository;
import com.flipcart.es.repository.SellerRepository;
import com.flipcart.es.repository.UserRepository;
import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.service.AuthService;
import com.flipcart.es.utility.MessageStructure;
import com.flipcart.es.utility.ResponseEntityProxy;
import com.flipcart.es.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService
{
	private UserRepository userRepository;
	private SellerRepository sellerRepository;
	private CustomerRepository customerRepository;
	private PasswordEncoder encoder;
	private CacheStore<String>otpCacheStore;
	private CacheStore<User>userCacheStore;
	private JavaMailSender javaMailSender;



	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> userRegister(UserRequest userRequest) 
	{
		if(userRepository.existsByEmail(userRequest.getEmail()))
			throw new  EmailAlreadyVarifiedException("user already existed with the specified email id");

		String OTP =generaateOTP();

		User user = mapToUserRequest(userRequest);
		userCacheStore.add(userRequest.getEmail(), user);
		otpCacheStore.add(userRequest.getEmail(), OTP);

		try
		{
			sendOtpToMail(user, OTP);
		}
		catch (Exception e) 
		{
			log.error("the email address doesn't exist");
		}


		return ResponseEntityProxy.setResponseStructure(HttpStatus.ACCEPTED,"please varify through OTP sent on email Id ", mapToUserResponse(user));

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

	@Override
	public ResponseEntity<String> verifyOTP(OtpModel otpModel) 
	{
		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());

		if(otp==null) throw new OTPExpiredException("OTP expired try again");
		if(user==null) throw new RegistrationSessionExpiredException("Registration session expired");
		if(!otp.equals(otpModel.getOtp())) throw new InvalidOTPException("invalid otp");
		user.setEmailVerified(true);
		userRepository.save(user);
		try {
			sendRegisstrationSuccessfully(user);
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}
		return new ResponseEntity<String>("Registration successful",HttpStatus.CREATED);

	}

	private void sendRegisstrationSuccessfully(User user) throws MessagingException 
	{
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("welcome to flipcart electronics")
				.sentDate(new Date())
				.text(
						"Hello "+user.getUsername()
						+"we welcome you to flipcart  electronics <br>"
						+"beware of fraudsters"
						+"have a nice shopping"
						+"with best regards"
						+"flipcart electronics"
						).build());



	}

	private void sendOtpToMail(User user,String otp) throws MessagingException
	{

		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("complete your registration to flipcart")
				.sentDate(new Date())
				.text(
						"hey, "+user.getUsername()
						+"good to see you intrested in flipkart,"
						+"complete your registration using the OTP <br>"
						+"<h1>"+otp+"</h1><br>"
						+"Note: the OTP expires in 1 minute"
						+"<br><br>"
						+"with best regards<br>"
						+"Flipkart"
						).build());

	}

	@Async
	private void sendMail(MessageStructure messageStructure) throws MessagingException
	{
		MimeMessage mimeMessage= javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
		messageHelper.setTo(messageStructure.getTo());
		messageHelper.setSubject(messageStructure.getSubject());
		messageHelper.setSentDate(messageStructure.getSentDate());
		messageHelper.setText(messageStructure.getText(),true);
		javaMailSender.send(mimeMessage);

	}

	private String generaateOTP()
	{
		return String.valueOf(new Random().nextInt(100000,999999));
	}

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

}
