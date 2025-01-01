package com.example.authentication.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authentication.domain.Role;
import com.example.authentication.domain.User1;
import com.example.authentication.repo.RoleRepo;
import com.example.authentication.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService,UserDetailsService {
 
    private final RoleRepo rolerepo;
    private final UserRepo userrepo;
    private final PasswordEncoder passwordEncoder;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public User1 saveUser(User1 user) {
        log.info("saving new user {} to repo",user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userrepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("saving new role {} to repo",role.getName());
        return rolerepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        log.info("Adding role {} to user {} ",rolename,username);
        User1 user=userrepo.findByUsername(username);
        Role role=rolerepo.findByname(rolename);
        user.getRoles().add(role);
    }

    @Override
    public User1 getUser(String username) {
        log.info("User is fetched with username {}",username);
        return userrepo.findByUsername(username);
    }

    @Override
    public List<User1> getUsers() {
        log.info("All user are fetched");
        return userrepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User1 user = userrepo.findByUsername(username);
        if(user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }
    
}
