package com.flipcart.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>
{

}
