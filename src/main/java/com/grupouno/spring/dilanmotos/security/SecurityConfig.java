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
<<<<<<< HEAD
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                
                // 1. RUTAS PÚBLICAS Y DE ERROR
                .requestMatchers(
                    "/login", "/register", "/forgot-password",
                    "/verify-code", "/reset-password", "/css/**", "/js/**", "/error/**"
                ).permitAll()
                
                // 2. MÓDULOS DE ADMINISTRACIÓN (Sincronizado con hasRole)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
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
=======
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login", "/register", "/forgot-password",
                                "/verify-code", "/reset-password", "/css/**", "/js/**",
                                "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // MANEJO DEL ERROR 401 (Unauthorized):
                        // Al poner .authenticated(), si un usuario intenta entrar a cualquier ruta
                        // sin estar logueado, Spring Security lanza internamente un 401.
                        // Automáticamente intercepta este 401 y redirige al usuario a la página de
                        // "/login".

                        .requestMatchers("/CuentaUsuario").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("correo")
                        .passwordParameter("contrasena")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                // MANEJO DEL ERROR 403 (Forbidden):
                // Ocurre cuando el usuario SI está logueado, pero intenta entrar a una ruta
                // que no le corresponde (ej. un usuario normal intentando entrar a
                // "/admin/**").
                // Aquí le decimos que en lugar de mostrar un error feo, lo mande a nuestra ruta
                // "/error/403".

                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403"))
                .build();
>>>>>>> origin/Modulo-Andres
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}