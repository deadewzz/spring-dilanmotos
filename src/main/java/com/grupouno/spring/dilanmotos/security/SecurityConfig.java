package com.grupouno.spring.dilanmotos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Recomendado deshabilitar mientras desarrollas
            .authorizeHttpRequests(auth -> auth
                
                // 1. RUTAS PÚBLICAS Y DE ERROR
                .requestMatchers(
                    "/login", "/register", "/forgot-password",
                    "/verify-code", "/reset-password", "/css/**", "/js/**", "/error/**"
                ).permitAll()
                
                // 2. LA MAGIA DEL PREFIJO: Todo lo que empiece con /admin/ requiere ser ADMIN
                // Usamos hasAuthority para que coincida exactamente con tu base de datos
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                
                // 3. RUTAS PROTEGIDAS GENERALES (Para usuarios normales)
                .requestMatchers("/CuentaUsuario", "/dashboard").authenticated()
                
                // 4. CANDADO FINAL
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403") 
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}