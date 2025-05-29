package com.luna.login_jwt_security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        // Obtener la información del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        logger.debug("Usuario autenticado: {}", auth.getName());
        logger.debug("Autoridades del usuario: {}", auth.getAuthorities());
        logger.debug("¿Está autenticado?: {}", auth.isAuthenticated());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "¡Endpoint protegido accedido exitosamente!");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        
        return ResponseEntity.ok(response);
    }
} 