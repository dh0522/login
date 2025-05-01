package com.example.login.auth.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	// 클라이언트의 토큰이 유효한지 검증하는 메서드
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

		// 1. HttpServletRequest 에서 토큰 추출
		String token = jwtTokenProvider.resolveToken(req.getHeader("Authorization"));

		if(isUsableAccessToken(token)){
			Authentication auth = jwtTokenProvider.getAuthentication(token);

			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(req, res);
	}

	public boolean isUsableAccessToken(String token){
		return token != null
			&& jwtTokenProvider.validateToken(token)
			&& jwtTokenProvider.hasRole(token)
			&& !jwtTokenProvider.isBlackListed(token);
	}
}
