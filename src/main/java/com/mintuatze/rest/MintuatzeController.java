package com.mintuatze.rest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mintuatze.services.SubscriptionService;
import com.mintuatze.services.SubscriptionService.WebPushMessage;

@RestController
@RequestMapping("/users")
public class MintuatzeController {

	@Autowired
	SubscriptionService subService;

	@PostMapping("/notify-all")
	public List<String> notifyAll(@RequestBody WebPushMessage message) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, JoseException {
		return subService.notifyAll(message);
	}	

}
