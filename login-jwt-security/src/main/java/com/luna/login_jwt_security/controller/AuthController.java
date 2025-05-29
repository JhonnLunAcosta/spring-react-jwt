package com.luna.login_jwt_security.controller;

import com.luna.login_jwt_security.dto.LoginRequest;
import com.luna.login_jwt_security.dto.LoginResponse;
import com.luna.login_jwt_security.dto.RegisterRequest;
import com.luna.login_jwt_security.model.User;
import com.luna.login_jwt_security.repository.UserRepository;
import com.luna.login_jwt_security.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            logger.debug("Intento de login para usuario: {}", request.getUsername());
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails.getUsername());
            
            logger.debug("Login exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
            
        } catch (BadCredentialsException e) {
            logger.error("Credenciales inválidas para usuario: {}", request.getUsername());
            return ResponseEntity.status(401).body("Credenciales inválidas");
        } catch (Exception e) {
            logger.error("Error en login para usuario: {}", request.getUsername(), e);
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupAdmin() {
        try {
            // Eliminar usuario existente si existe
            userRepository.findByUsername("admin").ifPresent(user -> userRepository.delete(user));
            
            // Crear nuevo usuario
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            
            User savedUser = userRepository.save(admin);
            logger.debug("Usuario admin creado con hash: {}", savedUser.getPassword());
            
            return ResponseEntity.ok("Usuario admin creado correctamente");
        } catch (Exception e) {
            logger.error("Error creando usuario admin", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.debug("Intento de registro para usuario: {}", request.getUsername());
            
            // Verificar si el usuario ya existe
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                logger.warn("Intento de registro con nombre de usuario existente: {}", request.getUsername());
                return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
            }
            
            // Verificar si el email ya existe
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Intento de registro con email existente: {}", request.getEmail());
                return ResponseEntity.badRequest().body("El correo electrónico ya está en uso");
            }
            
            // Crear nuevo usuario
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setFullName(request.getFullName());
            newUser.setRole("ROLE_USER"); // Rol por defecto
            
            User savedUser = userRepository.save(newUser);
            logger.info("Usuario registrado exitosamente: {}", savedUser.getUsername());
            
            // Generar token JWT
            String token = jwtService.generateToken(savedUser.getUsername());
            
            return ResponseEntity.ok(new LoginResponse(token));
            
        } catch (Exception e) {
            logger.error("Error en registro para usuario: {}", request.getUsername(), e);
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }
}
