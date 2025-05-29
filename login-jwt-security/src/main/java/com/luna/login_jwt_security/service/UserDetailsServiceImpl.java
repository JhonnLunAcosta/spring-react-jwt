package com.luna.login_jwt_security.service;

import com.luna.login_jwt_security.model.User;
import com.luna.login_jwt_security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Buscando usuario en la base de datos: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("Usuario no encontrado: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado");
            });

        logger.debug("Usuario encontrado: {} con rol: {}", user.getUsername(), user.getRole());
        logger.debug("Password hash en DB: {}", user.getPassword());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
