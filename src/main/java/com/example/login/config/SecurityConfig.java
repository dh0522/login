package com.example.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// SecurityFilterChain
	// filterchain 요청을 가로채어 허용된 사용자인지 확인하고 검증한다.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		http.csrf(csrf -> csrf.disable())
			.httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			.authorizeHttpRequests( auth->
				auth.anyRequest().permitAll()
			);

		return http.build();
	}

	// UserDetailsService
	// 사용자가 입력한 데이터를 통해 UserDetails 객체를 생성한다.



	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}


}
