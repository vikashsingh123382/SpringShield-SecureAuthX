package com.example.authentication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authentication.domain.User1;

public interface UserRepo extends JpaRepository<User1,Long> {
   User1 findByUsername(String Username);
}
