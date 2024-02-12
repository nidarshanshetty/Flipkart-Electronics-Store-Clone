package com.flipcart.es.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>
{

	Optional<AccessToken> findByToken(String at);

	

}
