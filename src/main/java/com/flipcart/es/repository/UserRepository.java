package com.flipcart.es.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>
{

	Optional<User> findByUsername(String username);
	
	List<User> findByIsEmailVerified(boolean b);

	boolean existsByEmail(String email);



}
