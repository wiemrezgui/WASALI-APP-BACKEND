package com.example.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.JwtAuthenticationFilter;
import com.example.service.CustomUserDetailsService;
 

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {
	
	 
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter; 
	
	
	
	@Bean
	 SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http.cors(Customizer.withDefaults());
		 http.csrf(AbstractHttpConfigurer::disable); 
		 http.authorizeHttpRequests((requests) -> requests
			 .requestMatchers("/api/auth/login", "/api/user/cin", "/api/auth/generate", "/api/role/rl", "/api/intervalleDistance/int", "/api/session/check", "/api/session/create", "/api/token/save", "/api/token/verify", "/api/token/email", "/error", "/api/user/createClient", "/api/user/createLivreur", "/api/user/email", "/api/user/updatePass", "/api/typeVehicule/type", "/api/jwt/check", "/api/gouv/gv", "/api/etat/get").permitAll()
		 	 .anyRequest().authenticated()); 
		 http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        	
            
     return http.build();
     
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("*"));
	    configuration.setAllowedMethods(Arrays.asList("*"));
	    configuration.setAllowedHeaders(Arrays.asList("*"));
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	 }
	 
	 @Bean
	 AuthenticationManager authenticationManager(
	            CustomUserDetailsService userDetailsService,
	            PasswordEncoder passwordEncoder) {
	        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	        authenticationProvider.setUserDetailsService(userDetailsService);
	        authenticationProvider.setPasswordEncoder(passwordEncoder);

	        return new ProviderManager(authenticationProvider);
	 }
	
	


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
  
    
    
    
    
	

}
