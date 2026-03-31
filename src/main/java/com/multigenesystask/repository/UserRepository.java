package com.multigenesystask.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.multigenesystask.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
    public User findByEmail(String email) throws UsernameNotFoundException;
	
	public List<User> findAllByOrderByCreatedAtDesc();

}
