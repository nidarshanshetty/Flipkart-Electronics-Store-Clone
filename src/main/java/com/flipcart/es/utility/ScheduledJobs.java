package com.flipcart.es.utility;

import org.springframework.stereotype.Component;

import com.flipcart.es.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledJobs
{
	private UserRepository userRepository;


	
	public void Cleanupnonverifiedusers()
	{
		userRepository.findByIsEmailVarified(false)
		.forEach(user->{
			user.setDeleted(true);
			userRepository.save(user);
		
		});

	}
}
