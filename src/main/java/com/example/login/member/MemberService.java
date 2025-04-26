package com.example.login.member;

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
public class MemberService {

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

}
