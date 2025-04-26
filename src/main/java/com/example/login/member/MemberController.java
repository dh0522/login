package com.example.login.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.member.dto.MemberJoinDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/join")
	public ResponseEntity<Long> join(@RequestBody @Valid MemberJoinDto joinDto){
		Long memberId = memberService.join(joinDto);
		return ResponseEntity.ok(memberId);
	}
}
