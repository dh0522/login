package com.example.login.member;

import org.springframework.stereotype.Service;

import com.example.login.member.dto.MemberJoinDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Long join(MemberJoinDto joinDto){

		// 1. 중복되는 email 존재 여부 확인.
		memberRepository.findByEmail(joinDto.getEmail()).orElseThrow(
			() -> new IllegalArgumentException("이미 존재하는 이메일입니다.")
		);

		// 2. 비밀번호 암호화하기



		return null;
	}

}
