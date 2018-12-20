package com.mintuatze.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mintuatze.security.AccountCredentials;
import com.mintuatze.services.SubscriptionService;
import com.mintuatze.services.SubscriptionService.WebPushSubscription;

@RestController
@RequestMapping("/security")
public class SecurityController {
	@Autowired
	SubscriptionService subService;

	@PostMapping("/login")
	public void signUp(@RequestBody AccountCredentials user) {
		System.out.println("User:" + user.getUsername() + " Password: ***");
	}

	@GetMapping("/me")
	public Authentication me() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		return authentication;
	}

	@PostMapping("/subscribe")
	public void subscribe(@RequestBody WebPushSubscription subscription) {
		subService.subscribe(subscription);
	}

	@PostMapping("/unsubscribe")
	public void unsubscribe(@RequestBody WebPushSubscription subscription) {
		subService.unsubscribe(subscription);
	}

}
