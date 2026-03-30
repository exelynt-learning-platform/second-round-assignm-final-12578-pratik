package com.multigenesystask.entity;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "first_name")
	    private String firstName;
	    
	    @Column(name = "last_name")
	    private String lastName;
	    
	    @JsonIgnore
	    @Column(name = "password")
	    private String password;

	    @Column(name = "email")
	    private String email;

//	    @Enumerated(EnumType.STRING)
	    private String role;
	    
	    private String mobile;

	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Address> addresses=new ArrayList<>();


	    @JsonIgnore
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
	    private List<Rating>ratings=new ArrayList<>();
	    
	    @JsonIgnore
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
	    private List<Review>reviews=new ArrayList<>();
	    
	    private LocalDateTime createdAt;

	
	
	
}
