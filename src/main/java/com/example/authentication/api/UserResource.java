package com.example.authentication.api;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.authentication.api.UserResource.RoleToUserForm;
import com.example.authentication.domain.Role;
import com.example.authentication.domain.User1;
import com.example.authentication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path="/api")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    
    private final UserService userservice;
    

    @GetMapping(path="/users")
    public ResponseEntity<List<User1>> getusers()
    {
        return ResponseEntity.ok().body(userservice.getUsers());
    }

    @PostMapping(path="/user/save")
    public ResponseEntity<User1> saveuser(@RequestBody User1 user)
    {
        return ResponseEntity.ok().body(userservice.saveUser(user));
    }

    @PostMapping(path="/role/save")
    public ResponseEntity<Role> saverole(@RequestBody Role role)
    {
        return ResponseEntity.ok().body(userservice.saveRole(role));
    }

    @PostMapping(path="/role/addtouser")
    public ResponseEntity<?> addtouser(@RequestBody RoleToUserForm form)
    {
        userservice.addRoleToUser(form.getUsername(), form.getRolename());
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/token/refresh")
    public void RefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String authorizationHeader = request.getHeader("AUTHORIZATION");
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    log.info("===============>AUTHORIZATION FILTER IS RUNNING for request {}",request.getServletPath());
                    String refresh_token = authorizationHeader.substring("Bearer ".length());
                    
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                    
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(refresh_token);
                    String username = decodedJWT.getSubject();

                    User1 user=userservice.getUser(username);
                    String access_token=JWT.create()
                           .withSubject(user.getUsername())
                           .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                           .withIssuer(request.getRequestURL().toString())
                           .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                           .sign(algorithm);
                    Map<String, String> tokens = new TreeMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("refresh_token", refresh_token);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);

                }catch (Exception exception) {
                    log.error("Error logging in: {}", exception.getMessage());
                    response.setHeader("error", exception.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", exception.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                    //response.sendError(FORBIDDEN.value());
                }
            }
            else
            {
                throw new RuntimeException("Refresh Token is missing");
            }
    }

    @Data
    class RoleToUserForm{
        String username;
        String rolename;
    }
    
}
