package com.example.authentication.filter;

import java.io.IOException;
import java.util.Date;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.authentication.domain.User1;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;
    
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager=authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        log.info("Username is {} and password is {}",username,password);
        UsernamePasswordAuthenticationToken authenticationtoken=new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationtoken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        User user=(User)authResult.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        String access_token=JWT.create()
                           .withSubject(user.getUsername())
                           .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                           .withIssuer(request.getRequestURL().toString())
                           .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                           .sign(algorithm);
        String refresh_token = JWT.create()
                           .withSubject(user.getUsername())
                           .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                           .withIssuer(request.getRequestURL().toString())
                           .sign(algorithm);
        

        //response.setHeader("access_token",access_token);
        //response.setHeader("refresh_token",refresh_token);
        Map<String, String> tokens = new TreeMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }


  
}
