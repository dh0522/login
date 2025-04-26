package com.example.login.member.dto;

import com.example.login.member.domain.Member;
import com.example.login.member.domain.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberJoinDto {

	@NotBlank(message = "이메일을 입력해주세요.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;

	@NotBlank(message = "이름을 입력해주세요")
	private String name;

	public Member toMember(MemberJoinDto dto){
		return Member.builder()
			.email(dto.getEmail())
			.password(dto.getPassword())
			.name(dto.getName())
			.role(Role.USER)
			.build();
	}
}
