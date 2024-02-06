package com.flipcart.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipcart.es.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer>
{

}
