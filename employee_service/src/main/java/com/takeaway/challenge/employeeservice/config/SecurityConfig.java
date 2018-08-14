//package com.takeaway.challenge.employeeservice.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	private BasicAuthenticationPoint basicAuthenticationPoint;
//
//	public SecurityConfig(BasicAuthenticationPoint basicAuthenticationPoint) {
//		this.basicAuthenticationPoint = basicAuthenticationPoint;
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable();
//		http.authorizeRequests().antMatchers("/employees/**").permitAll()
//				.anyRequest().authenticated();
//		http.httpBasic().authenticationEntryPoint(basicAuthenticationPoint);
//	}
//
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("takeaway").password("takeaway").roles("USER");
//	}
//}