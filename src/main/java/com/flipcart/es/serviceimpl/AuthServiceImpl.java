package com.flipcart.es.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipcart.es.cache.CacheStore;
import com.flipcart.es.entity.AccessToken;
import com.flipcart.es.entity.Customer;
import com.flipcart.es.entity.RefreshToken;
import com.flipcart.es.entity.Seller;
import com.flipcart.es.entity.User;
import com.flipcart.es.enums.UserRole;
import com.flipcart.es.exceptions.EmailAlreadyVarifiedException;
import com.flipcart.es.exceptions.InvalidOTPException;
import com.flipcart.es.exceptions.OTPExpiredException;
import com.flipcart.es.exceptions.RegistrationSessionExpiredException;
import com.flipcart.es.exceptions.UserNotLoggedInException;
import com.flipcart.es.exceptions.UserRoleNotFoundException;
import com.flipcart.es.repository.AccessTokenRepository;
import com.flipcart.es.repository.CustomerRepository;
import com.flipcart.es.repository.RefreshTokenRepository;
import com.flipcart.es.repository.SellerRepository;
import com.flipcart.es.repository.UserRepository;
import com.flipcart.es.requestdto.AuthRequest;
import com.flipcart.es.requestdto.OtpModel;
import com.flipcart.es.requestdto.UserRequest;
import com.flipcart.es.responsedto.AuthResponse;
import com.flipcart.es.responsedto.UserResponse;
import com.flipcart.es.security.JwtService;
import com.flipcart.es.service.AuthService;
import com.flipcart.es.utility.CookieManager;
import com.flipcart.es.utility.MessageStructure;
import com.flipcart.es.utility.ResponseEntityProxy;
import com.flipcart.es.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService
{
	private UserRepository userRepository;
	private SellerRepository sellerRepository;
	private CustomerRepository customerRepository;
	private PasswordEncoder encoder;
	private CacheStore<String>otpCacheStore;
	private CacheStore<User>userCacheStore;
	private JavaMailSender javaMailSender;
	private AuthenticationManager authenticationManager;
	private CookieManager cookieManager;
	private JwtService jwtService;
	private AccessTokenRepository accessTokenRepository;
	private RefreshTokenRepository refreshTokenRepository;




	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSeconds;




	public AuthServiceImpl(UserRepository userRepository, SellerRepository sellerRepository,
			CustomerRepository customerRepository, PasswordEncoder encoder, CacheStore<String> otpCacheStore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookieManager,JwtService jwtService,AccessTokenRepository accessTokenRepository,RefreshTokenRepository refreshTokenRepository) {
		super();
		this.userRepository = userRepository;
		this.sellerRepository = sellerRepository;
		this.customerRepository = customerRepository;
		this.encoder = encoder;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService=jwtService;
		this.accessTokenRepository=accessTokenRepository;
		this.refreshTokenRepository=refreshTokenRepository;

	}

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

	private AuthResponse mapToAuthResponse(User user)
	{
		return AuthResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.role(user.getUserRole().name())
				.isAuthenticated(true)
				.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
				.build();
	}


	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response) 
	{
		String username =authRequest.getEmail().split("@")[0];
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);
		if(!authentication.isAuthenticated())
		{
			throw new UsernameNotFoundException("Filed to authenticate the user");
		}
		else
		{
			//generating the cookies and  authResponse and returning to the client
			return userRepository.findByUsername(username).map(user->
			{
				grantAccess(response, user);

				return ResponseEntityProxy.setResponseStructure(HttpStatus.OK,"login successful",mapToAuthResponse(user));
			})
					.orElseThrow(()-> new UsernameNotFoundException("username not found"));

		}
	}
	private void grantAccess(HttpServletResponse  httpServletResponse,User user)
	{
		//generating access and refresh tokens
		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());

		//adding access and refresh tokens cookies to the response
		httpServletResponse.addCookie(cookieManager.configure(new Cookie("at", accessToken), accessExpiryInSeconds));
		httpServletResponse.addCookie(cookieManager.configure(new Cookie("rt", refreshToken), refreshExpiryInSeconds));

		//saving the access and refresh cookie in to the database
		accessTokenRepository.save(AccessToken.builder()
				.token(accessToken)
				.isBlocked(false)
				.user(user)
				.accessTokenExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());

		refreshTokenRepository.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.user(user)
				.refreshTokenExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());


	}

	@Override
	public ResponseEntity<ResponseStructure<String>> logout(String accessToken,String refreshToken, HttpServletResponse response) 
	{

		if(accessToken==null&&refreshToken==null)throw new  UserNotLoggedInException("user not logged in");


		accessTokenRepository.findByToken(accessToken)
		.ifPresent(at -> {
			at.setBlocked(true);
			accessTokenRepository.save(at);
		});


		refreshTokenRepository.findByToken(refreshToken)
		.ifPresent(rt -> {
			rt.setBlocked(true);
			refreshTokenRepository.save(rt);
		});

		response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));



		return ResponseEntityProxy.setResponseStructure(HttpStatus.OK,"logout successfully done", null);
	}


	private void blockAccessTokens(List<AccessToken> accessTokens)
	{
		for(AccessToken accessToken:accessTokens)
		{
			accessToken.setBlocked(true);
			accessTokenRepository.save(accessToken);
		}
	}
	private void blockRefreshTokens(List<RefreshToken> refreshTokens)
	{
		for(RefreshToken refreshToken:refreshTokens)
		{
			refreshToken.setBlocked(true);
			refreshTokenRepository.save(refreshToken);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> revokeAllDevice(HttpServletResponse response)
	{
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		userRepository.findByUsername(username)
		.ifPresent(user->{

			List<AccessToken> accessTokens = accessTokenRepository.findAllByUserAndIsBlocked(user,false);
			blockAccessTokens(accessTokens);
			List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserAndIsBlocked(user,false);
			blockRefreshTokens(refreshTokens);

		});

		response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));

		return ResponseEntityProxy.setResponseStructure(HttpStatus.OK,"revoke all device successfully done", null);
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> revokeOtherDevice(String refreshToken, String accessToken,
			HttpServletResponse response)
	{
		String username = SecurityContextHolder.getContext().getAuthentication().getName();


		userRepository.findByUsername(username)
		.ifPresent(user->{
			List<AccessToken> accessTokens = accessTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user,false,accessToken);
			blockAccessTokens(accessTokens);
			List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user,false,refreshToken);
			blockRefreshTokens(refreshTokens);
		});


		return ResponseEntityProxy.setResponseStructure(HttpStatus.OK,"revoke other device successfully done", null);
	}


}
