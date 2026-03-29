package com.multigenesystask.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multigenesystask.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
    public User findByEmail(String email);
	
	public List<User> findAllByOrderByCreatedAtDesc();

}
