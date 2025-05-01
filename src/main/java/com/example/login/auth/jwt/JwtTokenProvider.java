package com.example.login.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.login.member.domain.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {


	private final SecretKey secretKey; // JWT 토큰을 만들 때, "암호화" 를 위해 필요한 비밀키
	private final UserDetailsService userDetailsService; // 1000밀리초 = 1초 * 60 = 60초 * 15 = 15분
	private static final long ACCESS_TOKEN_EXP = 1000L * 60L * 15L ;
	private static final long REFRESH_TOKEN_EXP = 1000L * 60L * 60L * 24 ; // 하루
	private final RedisTemplate<String, String> redisTemplate;

	public JwtTokenProvider(@Value("${springboot.jwt.secret}") String secret,
							UserDetailsService userDetailsService,
							RedisTemplate<String, String> redisTemplate ){


					// 받은 문자열을 SecretKeySpec을 통해서 SecretKey로 변환
		this.secretKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8), // 우리가 String 자료형으루 갖고 있는 secret 을 byte[] 로 바꿔준다.
			"HmacSHA256" // 서명 알고리즘 = JWT 서명할 때 사용하는 해시 알고리즘 -> 즉, JWT 토큰을 변조되지 않았는지 우리가 만든게 맞는지 검증하는 게 서명
		);
		// 이 SecretKey 를 통해 JWT 토큰을 만들거나 검증할 수 있게 된다 .

		this.userDetailsService = userDetailsService;
		this.redisTemplate = redisTemplate;
	}


	// 1. AccessToken 생성 메소드
	public String createAccessToken(String username, Role role){

		Map<String, Object> claims = new HashMap<>();

		claims.put("username", username);
		claims.put("role", role.toString());

		return createToken(claims, ACCESS_TOKEN_EXP);
	}

	// 2. RefreshToken 생성
	public String createRefreshToken(String username){

		// refreshToken은 이미 생성되어 있는 토큰의 유효기간만 연장하면 되기 때문에 username 만 있으면 된다.
		Map<String, Object> claims = Map.of("username", username);

		String refreshToken = createToken(claims, REFRESH_TOKEN_EXP);

		/// redis 서버에 저장!!코드 짜야함!
		// redis 에 refreshToken:username 이라는 키로 refreshtoken을 저장하고, 유효기간 뒤에 만료되게 한다.
		redisTemplate.opsForValue().set("refreshToken:"+username, refreshToken, REFRESH_TOKEN_EXP, TimeUnit.MILLISECONDS );
		return refreshToken;
	}


	// 토큰을 생성해주는 메소드
	public String createToken(Map<String, Object> claims, long tokenExp){
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.addClaims(claims)
			.setId(Long.toString(System.nanoTime()))
			.setIssuedAt(new Date())
			.setExpiration(new Date( System.currentTimeMillis() + tokenExp))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	// 토큰 추출
	public String resolveToken(String bearerToken){
		if (bearerToken != null && bearerToken.startsWith("Bearer ")){
			return bearerToken.substring(7);
		}

		return null;
	}


	// token 의 유효기간이 만료되었다면 false , 유효하다면 true
	public boolean validateToken(String token){
		return !getClaims(token).getExpiration().before(new Date());
	}

	private Claims getClaims(String token){
		// token 에서 claim 만 따로 빼서 추출

		try{
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

		}catch(ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public boolean hasRole(String token){
		return getClaims(token).get("role") != null;
	}

	public String getUserName(String token){
		return getClaims(token).get("username").toString();
	}


	// Jwt 토큰을 복호화 해서 토큰에 들어있는 정보를 꺼내는 메소드
	public Authentication getAuthentication(String token){
		String username = getUserName(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		/**
		 * userDetails: 로그인한 유저 정보
		 *
		 * "": 비밀번호는 필요 없으니까 빈 문자열 넣음 (비밀번호 검증은 이미 완료됐으니까)
		 *
		 * userDetails.getAuthorities(): 유저가 가진 권한 목록 (ex: ROLE_USER)
		 */
		// 꺼낸 정보를 바탕으로 SecurityContextHolder 에 들어갈 Authentication 객체 생성
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities() );
	}

	public boolean isBlackListed(String token){
		String key = "BlackList:" + getClaims(token).get("username").toString();

		return redisTemplate.hasKey(key);
	}

	// 로그아웃 하면 블랙리스트에 추가됨!
	// 내가 로그아웃을 하면, accessToken은 만료가 안됨! 그래서 내 토큰을 블랙리스트에 추가하는 것
	// 그 블랙리스트의 추가 시간은 토큰이 만료되기 까지 남은 시간으로 설정 ! 그 이후엔 어차피 만료되서 권한이 없으니까!
	public void addBlackList(String token){

		long tokenCreatedTime = getClaims(token).getIssuedAt().getTime();

		// (원래 만료될 시간) - 현재시간 = 토큰이 만료되기 까지 남은 시간
		long blackListExp = (tokenCreatedTime + ACCESS_TOKEN_EXP) - System.currentTimeMillis();

		redisTemplate.opsForValue().set(
			"BlackList:" + getClaims(token).getId(),
			"true",
			blackListExp,
			TimeUnit.MILLISECONDS);
	}

	public void deleteRefreshToken(String token){
		String username = getUserName(token);

		redisTemplate.delete("refreshToken:"+username);
	}


	public boolean isValidRefreshToken(String token){
		String username = getUserName(token);

		redisTemplate.delete("refreshToken:"+username);

		String storedRefresh = redisTemplate.opsForValue().get("refreshToken:" + username);

		return storedRefresh != null && storedRefresh.equals(token);
	}


}
