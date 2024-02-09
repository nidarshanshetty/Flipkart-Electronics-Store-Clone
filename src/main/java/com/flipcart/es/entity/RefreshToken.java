package com.flipcart.es.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long tokenId;
	private String token;
	private boolean  isBlocked;
	private LocalDateTime refreshTokenExpiration;
}
