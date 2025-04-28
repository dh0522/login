package com.example.login.member;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.login.member.domain.Member;
import com.example.login.member.domain.Role;
import com.example.login.member.dto.MemberJoinDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService{

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public void join(MemberJoinDto joinDto){

		String userEmail = joinDto.getEmail();
		String pwd = joinDto.getPassword();

		// 1. 중복되는 email 존재 여부 확인.
		if (memberRepository.findByEmail(userEmail).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}


		// 2. 비밀번호 암호화하기
		String encodePwd = bCryptPasswordEncoder.encode(pwd);

		Member member = Member.builder()
			.name(joinDto.getName())
			.password(encodePwd)
			.email(userEmail)
			.role(Role.USER)
			.build();

		memberRepository.save(member);
	}


	// 로그인할 때 사용하는 클래스 -> 시큐리티가 로그인할 때 사용자 정보를 찾아오려고 부르는 서비스(UserDetailsService 구현체)
	// 즉, 로그인할 때 DB에서 사용자 꺼내서 인증 정보를 만들어주는 역할을 한다.
	// 시큐리티 문법만 사용할 경우, 로그인을 따로 구현할 필요가 없다.

}
