package com.example.login.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDto {

	@Email
	@NotBlank(message = "이메일 입력은 필수입니다.")
	private String email;


	@NotBlank(message = "비밀번호 입력은 필수입니다.")
	private String password;
}
