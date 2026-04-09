package com.example;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.component.JwtTokenProvider;
import com.example.service.CustomUserDetailsService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	
	 private final JwtTokenProvider jwtTokenProvider;
	 private final CustomUserDetailsService userDetailsService;

	 public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
	      this.jwtTokenProvider = jwtTokenProvider;
	      this.userDetailsService = userDetailsService;
	 }
	
	@Override
	protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException, java.io.IOException {

        String token = getTkFromRequest(req);

        if(StringUtils.hasText(token) && jwtTokenProvider.validateTk(token)){

            String email = jwtTokenProvider.getEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationTk = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationTk.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authenticationTk);
        }

        filterChain.doFilter(req, res);
    }

    private String getTkFromRequest(HttpServletRequest req){

        String bearerTk = req.getHeader("Authorization");
        if(StringUtils.hasText(bearerTk) && bearerTk.startsWith("Bearer ")){
            return bearerTk.substring(7);
        }

        return null;
    }
	
}
