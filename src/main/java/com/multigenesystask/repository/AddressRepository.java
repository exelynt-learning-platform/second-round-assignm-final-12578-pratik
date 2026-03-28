package com.multigenesystask.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multigenesystask.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
	
	

}
