package com.flipcart.es.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>
{

	boolean existsByEmail(String email);

	 Optional<User> findByUsername(String username);


}
