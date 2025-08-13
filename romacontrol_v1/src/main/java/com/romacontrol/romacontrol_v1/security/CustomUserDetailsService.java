// security/CustomUserDetailsService.java
package com.romacontrol.romacontrol_v1.security;

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
    Usuario u = usuarioRepo.findByDni(dni)
        .orElseThrow(() -> new UsernameNotFoundException("DNI no encontrado"));

    var auths = u.getRoles() == null ? java.util.List.<GrantedAuthority>of()
        : u.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombre().toUpperCase()))
            .collect(Collectors.toList());

    // El campo pin de DB puede venir con {noop}... o {bcrypt}...
    return User.builder()
        .username(u.getDni())
        .password(u.getPin())
        .authorities(auths)
        .accountLocked(!u.isActivo())
        .disabled(!u.isActivo())
        .build();
  }
}
