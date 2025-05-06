package com.example.login.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.auth.AuthService;
import com.example.login.auth.jwt.dto.JwtToken;
import com.example.login.member.dto.MemberJoinDto;
import com.example.login.member.dto.MemberLoginDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final AuthService authService;

	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody @Valid MemberJoinDto joinDto){

		memberService.join(joinDto);

		return ResponseEntity.ok("회원가입 완료");
	}

	@PostMapping("/login")
	public ResponseEntity<JwtToken> login(@RequestBody @Valid MemberLoginDto memberLoginDto){

		String email = memberLoginDto.getEmail();
		String password = memberLoginDto.getPassword();

		JwtToken token = authService.login(email, password);

		return ResponseEntity.ok(token);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout (@RequestHeader("Authorization") String token) {
		System.out.println("TTTTT token = "+token);
		authService.logout(token);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<JwtToken> refreshToken(@RequestHeader("Authorization") String token){
		JwtToken refreshToken = authService.refresh(token);

		return ResponseEntity.ok(refreshToken);
	}


}
