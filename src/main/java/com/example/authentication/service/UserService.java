package com.example.authentication.service;

import java.util.List;

import com.example.authentication.domain.Role;
import com.example.authentication.domain.User1;

public interface UserService {
  User1 saveUser(User1 user);
  Role saveRole(Role role);
  void addRoleToUser(String username,String rolename);
  User1 getUser(String username);
  List<User1>getUsers();
}
