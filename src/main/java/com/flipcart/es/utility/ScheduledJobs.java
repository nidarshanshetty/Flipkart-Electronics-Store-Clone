package com.flipcart.es.utility;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipcart.es.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledJobs
{
	
	private UserRepository userRepository;


	@Scheduled(fixedDelay = 10000l)
	public void Cleanupnonverifiedusers()
	{
		userRepository.findByIsEmailVerified(false)
		.forEach(user->{
			user.setDeleted(true);
			userRepository.save(user);
			userRepository.delete(user);
		});
	}
}
