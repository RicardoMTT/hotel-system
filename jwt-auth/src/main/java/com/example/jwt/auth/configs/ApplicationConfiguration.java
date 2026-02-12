package com.example.jwt.auth.configs;

import com.example.jwt.auth.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
* Para sobreescribir la autenticacion basica HTTP
* usamos este archivo
* */
@Configuration
public class ApplicationConfiguration {

    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    * Define como buscar el usuario en base al username en este
    * caso el email usando el userRepository inyectado
    * */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /*
    * Usado para codificar la contraseña,
    * Define el codificador de contraseñas que se usará para comparar las contraseñas ingresadas
    * por el usuario con las almacenadas en la base de datos
    * */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
    * Establece la nueva estrategia para realizar la autenticacion que se llama en el service authenticationManager.authenticate(
    * */
    @Bean
    AuthenticationProvider authenticationProvider() {
        // Para autenticar usuarios usa este repositorio y este encoder
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

}
