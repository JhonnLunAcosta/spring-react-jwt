package com.luna.login_jwt_security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordVerifier {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // La contraseña que intentamos usar
        String rawPassword = "admin123";
        
        // El hash que está en la base de datos (reemplazar con el hash actual de tu BD)
        String storedHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        
        // Generar un nuevo hash
        String newHash = encoder.encode(rawPassword);
        
        // Verificar si la contraseña coincide con el hash almacenado
        boolean matches = encoder.matches(rawPassword, storedHash);
        
        System.out.println("Contraseña a probar: " + rawPassword);
        System.out.println("Hash almacenado: " + storedHash);
        System.out.println("Nuevo hash generado: " + newHash);
        System.out.println("¿La contraseña coincide con el hash almacenado?: " + matches);
    }
} 