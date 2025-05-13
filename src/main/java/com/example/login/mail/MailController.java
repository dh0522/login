package com.example.login.mail;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MailController {


	private final MailService mailService;


	@RequestMapping("/mail")
	public ResponseEntity<?> sendMail(HttpServletRequest req) {

		String result = "";

		try{

			result = mailService.sendMail(String.valueOf(req.getRequestURL()));

		}catch (Exception e){
			return ResponseEntity.badRequest().body(e.getMessage());
		}


		return ResponseEntity.ok(result);
	}
}
