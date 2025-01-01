package com.example.authentication.security;

import javax.swing.text.html.FormSubmitEvent.MethodType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authentication.filter.CustomAuthenticationFilter;
import com.example.authentication.filter.CustomAuthorizationFilter;

import lombok.AllArgsConstructor;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig  {
   
    @Autowired
    private final AuthenticationConfiguration authenticationConfiguration;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

            http.csrf(customizer -> customizer.disable());
            http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            //http.headers(customizer -> customizer.disable());
            //http.authorizeHttpRequests(request -> request.anyRequest().authenticated());
            http.authorizeHttpRequests(request -> request.requestMatchers("/login","/api/token/refresh").permitAll());
            http.authorizeHttpRequests(request -> request.requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER"));
            http.authorizeHttpRequests(request -> request.requestMatchers("/api/user/save/**").hasAnyAuthority("ROLE_ADMIN"));
            http.authorizeHttpRequests(request -> request.anyRequest().authenticated());
            //http.authorizeHttpRequests(request -> request.anyRequest().permitAll());
            //http.formLogin(Customizer.withDefaults());  //to access through browser form
            http.httpBasic(Customizer.withDefaults()); //to access through postman
            //http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.addFilter(new CustomAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()));
            http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        
    }
}
