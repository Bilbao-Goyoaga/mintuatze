package com.mintuatze.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service(value = "TokenAuthenticationService")
class TokenAuthenticationService {
	public static final SecretKey signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String ROLES_CLAIM = "rolesClaim";
    
	
	void addAuthentication(HttpServletResponse res, Authentication auth) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		Collection <String>rolesStr = new ArrayList<String>();
		auth.getAuthorities().forEach(action -> rolesStr.add(action.toString()));

		HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put(ROLES_CLAIM, String.join(",", rolesStr));
		
		// Let's set the JWT Claims
		final String jwt = Jwts.builder().setSubject(auth.getName()).setIssuedAt(now).addClaims(claims)
				.setExpiration(new Date(nowMillis + EXPIRATION_TIME)).signWith(signingKey, SignatureAlgorithm.HS256)
				.compact();

		res.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
	}

	Authentication getAuthentication(HttpServletRequest request) throws IOException {

		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			Claims body = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

			String subject = body.getSubject();
			
			if (subject != null) {
				String rolesStr = (String)body.get(ROLES_CLAIM);
				return new UsernamePasswordAuthenticationToken(subject, null, AuthorityUtils.createAuthorityList(rolesStr.split(",")));
			}
			return null;
		}
		return null;

	}
}