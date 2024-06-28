package com.sds.apitodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

//스프링 3.0 부터 스프링빈 xml 파일을 대신하여 어노테이션 기반으로 설정할 수 있도록 지원..
/*
 <bean id="msg" class="SecurityFilterChain">
 	<constructor-args value="바보"/>
 </bean> 
 */ 
@Configuration
public class SecurityConfig {
	
	//스프링이 지원하는 단방향 암호화(해시) 객체 등록
	@Bean 
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//우리가 spring mvc 에서 AOP를 이용하여 uri를 걸러낸 작업을 스프링 부트 시큐리티에서는 보다 쉽고 체계적으로
	//지원...
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		
		//필터링 할 uri 명시..
		httpSecurity
			.authorizeHttpRequests(
				(auth) -> auth
				
				.anyRequest().permitAll()
			);
	
		
		
		
		//토큰 비활성화 
		httpSecurity.csrf((auth)->auth.disable());
		
		return httpSecurity.build();
	}

}











