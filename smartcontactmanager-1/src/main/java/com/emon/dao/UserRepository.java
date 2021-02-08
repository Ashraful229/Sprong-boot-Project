package com.emon.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emon.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>
{

	@Query("select u from User u where u.email=:email")
	public User getUserByUserNamr(@Param("email") String email); 
		
	
}
