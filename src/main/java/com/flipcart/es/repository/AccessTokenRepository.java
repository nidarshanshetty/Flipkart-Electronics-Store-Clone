package com.flipcart.es.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>
{

	Optional<AccessToken> findByToken(String at);

	List<AccessToken> findByAccessTokenExpirationBefore(LocalDateTime now);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);



}
