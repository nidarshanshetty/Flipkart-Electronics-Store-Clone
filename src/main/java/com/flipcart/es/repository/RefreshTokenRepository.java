package com.flipcart.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{

}
