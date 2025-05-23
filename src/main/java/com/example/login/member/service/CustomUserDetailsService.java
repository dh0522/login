package com.example.login.member.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.login.member.MemberRepository;
import com.example.login.member.domain.Member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new EntityNotFoundException("사용자를 찾을 수 없습니다.")
		);

		return User.builder()
			.username(member.getEmail())
			.password(member.getPassword())
			.roles(member.getRole().name())
			.build();
	}
}
