package com.flipcart.es.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{

	Optional<RefreshToken> findByToken(String rt);

}
