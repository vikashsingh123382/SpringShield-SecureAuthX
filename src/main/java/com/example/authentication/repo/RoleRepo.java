package com.example.authentication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authentication.domain.Role;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Role findByname(String name);
 }
