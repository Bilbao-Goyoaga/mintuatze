package com.mintuatze;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mintuatze.security.JWTAuthenticationFilter;
import com.mintuatze.security.JWTLoginFilter;
import com.mintuatze.security.UsersService;

/**
 * 
 * @author kbbkb
 * Documentacion: https://github.com/auth0-blog/spring-boot-jwts/tree/master/src/main/java/com/example/security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    public static final String SIGN_UP_URL = "/security/login";
    
    @Autowired
    UsersService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	PasswordEncoder passwordEncoder = passwordEncoder();
    	System.out.println(passwordEncoder.encode("bilbao"));
    	System.out.println(passwordEncoder.encode("vesga"));
    	System.out.println(passwordEncoder.encode("ferrandez"));
    	System.out.println(passwordEncoder.encode("celaya"));
    	
        http.cors().and().
        		csrf().disable().authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                // We filter the api/login requests
                .addFilterBefore(new JWTLoginFilter(SIGN_UP_URL, authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                // And filter other requests to check the presence of JWT in header
                .addFilterBefore(new JWTAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*").allowCredentials(true).exposedHeaders("Authorization");
    }
    
    
    @Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		Collection<UserDetails> users = userService.getUsers();
		InMemoryUserDetailsManager imudm = new InMemoryUserDetailsManager(users);

		auth.userDetailsService(imudm);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}