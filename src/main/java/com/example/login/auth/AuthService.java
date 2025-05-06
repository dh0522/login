package com.example.login.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.login.auth.jwt.JwtTokenProvider;
import com.example.login.auth.jwt.dto.JwtToken;
import com.example.login.member.MemberRepository;
import com.example.login.member.domain.Member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
// 	로그인 , 로그아웃 구현할 서비스

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;


	public JwtToken login(String email, String password){

		// 1. login을 하면 repo에서 해당 email을 가진 member가 있는지 검증
		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new EntityNotFoundException("해당하는 사용자가 없습니다.")
		);

		// 2. DB에서 가져온 member의 비밀번호와 클라이언트가 입력한 비밀번호가 동일한지 검증 (plain, encoded password)
		if( !passwordEncoder.matches(password, member.getPassword())){
			throw new IllegalArgumentException("비밀번호가 틀립니다!");
		}

		// 3. 존재하면, jwt token을 생성해서 클라이언트로 넘겨준다.
		return new JwtToken( jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole()), jwtTokenProvider.createRefreshToken(member.getEmail()));
	}


	public void logout(String token){

		System.out.println("로그아웃 시작");


		String accessToken = jwtTokenProvider.resolveToken(token);

		System.out.println("accessToken = " + accessToken);

		if (!jwtTokenProvider.validateToken(accessToken)){
			throw new IllegalArgumentException("이미 만료된 토큰입니다.");
		}

		jwtTokenProvider.addBlackList(accessToken);
		jwtTokenProvider.deleteRefreshToken(accessToken);
	}


	public JwtToken refresh(String token){
		//  refresh 토큰의 정보를 통해서 AccessToken 을 재발급 받는다.

		// 1. 토큰에서 refresh 토큰 추출
		String refreshToken = jwtTokenProvider.resolveToken(token);

		// 2. refresh 토큰이 만료되었는지 검증
		if( refreshToken == null || ! jwtTokenProvider.validateToken(refreshToken) ){
			throw new IllegalArgumentException("해당 토큰은 만료되었습니다.");
		}

		// 3. 추출한 refresh 토큰에서 정보 추출
		String email = jwtTokenProvider.getUserName(refreshToken);

		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다.")
		);

		// 4. refresh 토큰이 유효한지 검증
		if(!jwtTokenProvider.isValidRefreshToken(refreshToken)){
			throw new IllegalArgumentException("해당 토큰은 유효하지 않습니다.");
		}

		return new JwtToken(
			jwtTokenProvider.createAccessToken(email, member.getRole()),
			refreshToken
		);
	}









}
