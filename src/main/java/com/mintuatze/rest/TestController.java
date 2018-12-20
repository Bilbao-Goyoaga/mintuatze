package com.mintuatze.rest;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mintuatze.security.AccountCredentials;

@RestController
@RequestMapping("/test")
public class TestController {
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting/admin")
	@PreAuthorize("hasAnyRole('admin')")
	public Greeting greetingAdmin() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		return new Greeting(counter.incrementAndGet(), String.format(template, authentication.getName()));
	}

	@RequestMapping("/greeting/user")
	@PreAuthorize("hasAnyRole('user')")
	public Greeting greetingUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		return new Greeting(counter.incrementAndGet(), String.format(template, authentication.getName()));
	}

	@RequestMapping("/greeting/all")
	@PreAuthorize("hasAnyRole('user', 'admin')")
	public Greeting greetingAll() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		return new Greeting(counter.incrementAndGet(), String.format(template, authentication.getName() + "-all"));
	}

	@RequestMapping("/noauthorization")
	public Authentication noAuthorization(Principal principal) {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		
		return authentication;
	}

	public class Greeting {

		private final long id;
		private final String content;

		public Greeting(long id, String content) {
			this.id = id;
			this.content = content;
		}

		public long getId() {
			return id;
		}

		public String getContent() {
			return content;
		}
	}

}
