package com.mintuatze.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service(value = "SubscriptionService")
public class SubscriptionService {
	private static PushService pushService;

	@Value("${dominio}")
	private String dominio;

	@Autowired
	private ObjectMapper objectMapper;

	private Map<String, WebPushSubscription> subscriptions = new ConcurrentHashMap<>();

	public SubscriptionService () throws GeneralSecurityException {
		pushService = new PushService("BPxfEPipIGHBXntXPadnOb_fgjAgRkwyJkH1NiR6loR9LWuAkQNuVky0FN9-TKw3NOZmfeNlKx0pgnJOJGw6ObM=", "AJ9NJUqHgnC1NR7iFQrg_79QLJT0fo7j3UBZfyIfXrtT", "kbbkbb@hotmail.com");
		
	}
	
	public void subscribe(WebPushSubscription subscription) {
		subscriptions.put(subscription.endpoint, subscription);
	}

	public void unsubscribe(WebPushSubscription subscription) {
		subscriptions.remove(subscription.endpoint);
	}

	public List<String> notifyAll(@RequestBody WebPushMessage message)
			throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, JoseException {
		List <String> rdo = new ArrayList<String>();
		
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		String payload = "{" + 
				"  \"notification\": {" + 
				"    \"title\": \"Nuevo envío\"," + 
				"    \"actions\": [" + 
				"      {" + 
				"        \"action\": \"descargar_envio\"," + 
				"        \"title\": \"Descargar envío\"" + 
				"      }," + 
				"      {" + 
				"        \"action\": \"abrir_aplicacion\"," + 
				"        \"title\": \"Abrir aplicación\"" + 
				"      }" + 
				"    ]," + 
				"    \"body\": \"Hay cambios en las reservas por " + authentication.getName() + "\"," + 
				"    \"icon\": \"" + dominio + "/assets/icons/icon-72x72.png\"," + 
				"    \"badge\": \"" + dominio + "/assets/icons/icon-72x72.png\"," + 
				"    \"lang\": \"es\"," + 
				"    \"renotify\": true," + 
				"    \"requireInteraction\": true," + 
				"    \"tag\": 926796012340920300," + 
				"    \"vibrate\": [" + 
				"      300," + 
				"      100," + 
				"      400" + 
				"    ]," + 
				"    \"data\": {" + 
				"      \"url\": \"https://twitter.com/statuses/926796012340920321\"," + 
				"      \"created_at\": \"Sat Nov 04 12:59:23 +0000 2017\"," + 
				"      \"favorite_count\": 0," + 
				"      \"retweet_count\": 0" + 
				"    }" + 
				"  }" + 
				"}";
		
		for (WebPushSubscription subscription : subscriptions.values()) {

			Notification notification = new Notification(subscription.getEndpoint(),
					subscription.getKeys().get("p256dh"), subscription.getKeys().get("auth"), payload);

			HttpResponse response = pushService.send(notification);
			System.out.println(response);
		}

		return rdo;
	}

	public static class WebPushSubscription {
		public String getEndpoint() {
			return endpoint;
		}
		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
		public HashMap <String, String> getKeys() {
			return keys;
		}
		public void setKeys(HashMap <String, String> keys) {
			this.keys = keys;
		}
		private String endpoint;
		private HashMap <String, String>keys;

	}

	public static class WebPushMessage {
		public String title;
		public String clickTarget;
		public String message;
	}

}
