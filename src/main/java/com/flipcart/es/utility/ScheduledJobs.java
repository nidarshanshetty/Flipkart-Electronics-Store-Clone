package com.flipcart.es.utility;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipcart.es.entity.AccessToken;
import com.flipcart.es.repository.AccessTokenRepository;
import com.flipcart.es.repository.RefreshTokenRepository;
import com.flipcart.es.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledJobs
{

	private UserRepository userRepository;
	private AccessTokenRepository accessTokenRepository;
	private RefreshTokenRepository refreshTokenRepository;


	@Scheduled(fixedDelay = 10000l)
	public void CleanUpNonVerifiedUsers()
	{
		userRepository.findByIsEmailVerified(false)
		.forEach(user->{
			user.setDeleted(true);
			userRepository.save(user);
			userRepository.delete(user);
		});
	}

	@Scheduled(fixedDelay = 10000l)
	public void CleanUpExpiredTokens()
	{
		List<AccessToken> accessTokens = accessTokenRepository.findByAccessTokenExpirationBefore(LocalDateTime.now());

		for(AccessToken accessToken:accessTokens)
		{
			accessTokenRepository.delete(accessToken);
		}

		refreshTokenRepository.findByRefreshTokenExpirationBefore(LocalDateTime.now())
		.forEach(refreshToken->{
			refreshTokenRepository.delete(refreshToken);
		});
	}


}
