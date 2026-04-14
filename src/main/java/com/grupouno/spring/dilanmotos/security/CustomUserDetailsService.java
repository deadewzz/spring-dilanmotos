package com.grupouno.spring.dilanmotos.security;

import com.grupouno.spring.dilanmotos.models.Usuarios;
import com.grupouno.spring.dilanmotos.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar usuarios desde la base de datos.
 *
 * <p>Implementa {@link UserDetailsService} para que Spring Security
 * pueda autenticar usuarios usando su correo electrónico como nombre de usuario.</p>
 *
 * @author Neyder Estiben Manrique Alvarez
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuarios usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return User.builder()
                .username(usuario.getCorreo())                 
                .password(usuario.getContrasena())             
                
                .roles(usuario.getRol() != null ? usuario.getRol() : "USER")
                
                .disabled(!usuario.isHabilitado())        
                .build();
    }
}