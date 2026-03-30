package com.multigenesystask.config.jwt;




import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.multigenesystask.service.CustomUserDetailsService;

import java.io.IOException;
@Component

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 
	@Autowired
    private JwtUtils jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	 String jwt = jwtTokenProvider.getJwtFromHeader(request);

         if (jwt != null) {
             if (jwtTokenProvider.validateToken(jwt)) {
                 try {
                     String username = jwtTokenProvider.getUserNameFromJwtToken(jwt);
                     UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                     UsernamePasswordAuthenticationToken authToken =
                             new UsernamePasswordAuthenticationToken(
                                     userDetails, null, userDetails.getAuthorities());

                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     SecurityContextHolder.getContext().setAuthentication(authToken);

                 } catch (Exception e) {
                     log.error("Cannot set user authentication: {}", e.getMessage());
                     response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
                     return; // ← stop the filter chain, don't let the request through
                 }
             }
             // if validateToken() returned false, we simply don't set authentication
             // Spring Security will deny the request on its own via 401
         }

         filterChain.doFilter(request, response);
    }
}
