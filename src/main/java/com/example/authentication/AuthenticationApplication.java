package com.example.authentication;
import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.authentication.domain.Role;
import com.example.authentication.domain.User1;
import com.example.authentication.service.UserService;

@SpringBootApplication
public class AuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new User1(null, "John Travolta", "john", "1234", new ArrayList<>()));
			userService.saveUser(new User1(null, "Will Smith", "will", "1234", new ArrayList<>()));
			userService.saveUser(new User1(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
			userService.saveUser(new User1(null, "Arnold Schwarzenegger", "arnold", "1234", new ArrayList<>()));

			userService.addRoleToUser("john", "ROLE_USER");
			userService.addRoleToUser("will", "ROLE_MANAGER");
			userService.addRoleToUser("jim", "ROLE_ADMIN");
			userService.addRoleToUser("arnold", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("arnold", "ROLE_ADMIN");
			userService.addRoleToUser("arnold", "ROLE_USER");
		};
	}
	
}
