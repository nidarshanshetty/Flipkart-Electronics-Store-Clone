package com.flipcart.es.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.RefreshToken;
import com.flipcart.es.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{

	Optional<RefreshToken> findByToken(String rt);

	List<RefreshToken> findByRefreshTokenExpirationBefore(LocalDateTime now);

	List<RefreshToken> findAllByUserAndIsBlocked(User user,boolean b);

	List<RefreshToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String refreshToken);

}
