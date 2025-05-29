package com.luna.login_jwt_security.config;

import com.luna.login_jwt_security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.debug("Auth Header: {}", authHeader);
        logger.debug("Request URI: {}", request.getRequestURI());
        logger.debug("Request Method: {}", request.getMethod());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No token found or invalid format");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            logger.debug("JWT Token: {}", jwt);
            logger.debug("Username from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.debug("User details loaded: {}", userDetails.getUsername());
                logger.debug("User authorities: {}", userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    logger.debug("Token is valid for user: {}", username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication successful");
                } else {
                    logger.debug("Token validation failed for user: {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
            logger.error("Error details: ", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
