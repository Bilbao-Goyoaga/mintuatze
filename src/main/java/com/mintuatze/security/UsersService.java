package com.mintuatze.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
	private static final String USUARIOS = "usuarios.properties";

	private static final Collection<GrantedAuthority> adminsAuths = AuthorityUtils.createAuthorityList("ROLE_admin");
	private static final Collection<GrantedAuthority> userAuths = AuthorityUtils.createAuthorityList("ROLE_user");

	@Value("${directorio.data}")
	private String dirDatos;

	private Resource loadAsResource(String path) throws MalformedURLException {
		Path file = Paths.get(path);
		return new UrlResource(file.toUri());
	}

	public Collection<UserDetails> getUsers() throws IOException {
		Collection<UserDetails> rdo  = new ArrayList <UserDetails>();

		Resource res = loadAsResource(dirDatos + "/" + USUARIOS);
		Properties p = new Properties();
		p.load(res.getInputStream());

		List<String> usersAdmin = Arrays.asList(p.getProperty("roles.admin").split(","));
		p.remove("roles.admin");

		Set<String> userNames = p.stringPropertyNames();
		for (String userName : userNames) {
			AccountCredentials ac = new AccountCredentials();
			ac.setUsername(userName);
			ac.setPassword(p.getProperty(userName));

			ac.setAuthorities(usersAdmin.contains(userName) ? adminsAuths : userAuths);
			
			rdo.add(ac);
		}

		return rdo;
	}

}
