package com.romacontrol.romacontrol_v1.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        // 🔹 1. Buscar usuario por DNI
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("DNI no encontrado: " + dni));

        // 🔹 2. Construir authorities (roles)
        List<GrantedAuthority> authorities = (u.getRoles() == null)
                ? List.of()
                : u.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombre().toUpperCase()))
                        .collect(Collectors.toList());

        // 🔹 3. Devolver el usuario con el PIN (ya encriptado con BCrypt)
        return User.builder()
                .username(u.getDni())
                .password(u.getPin()) // debe contener el hash BCrypt
                .authorities(authorities)
                .accountLocked(!u.isActivo())
                .disabled(!u.isActivo())
                .build();
    }
}
