// web/AuthController.java
package com.romacontrol.romacontrol_v1.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;  // <-- NUEVO

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final SecurityContextRepository securityContextRepository;
  private final UsuarioRepository usuarioRepo; // <-- NUEVO

  @PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest req,
                           HttpServletRequest request,
                           HttpServletResponse response) {
  try {
    // Autentica por DNI + PIN
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getDni(), req.getPin())
    );

    // Crea y fija el SecurityContext
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);

    // Asegura que exista sesión => permite Set-Cookie JSESSIONID
    request.getSession(true);

    // Persiste el contexto en la sesión
    securityContextRepository.saveContext(context, request, response);

    return new LoginResponse(true, req.getDni());

  } catch (org.springframework.security.authentication.BadCredentialsException e) {
    // ⚠️ Credenciales incorrectas → devolvemos 401
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
  } catch (Exception e) {
    // ⚠️ Cualquier otro error inesperado → 500
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno en login");
  }
}

  /*@PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest req,
                             HttpServletRequest request,
                             HttpServletResponse response) {
    // Autentica por DNI + PIN
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getDni(), req.getPin())
    );

    // Crea y fija el SecurityContext
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);

    // Asegura que exista sesión => permite Set-Cookie JSESSIONID
    request.getSession(true);

    // Persiste el contexto en la sesión
    securityContextRepository.saveContext(context, request, response);

    return new LoginResponse(true, req.getDni());
  }*/

  @PostMapping("/logout")
  public void logout(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session != null) session.invalidate();
    SecurityContextHolder.clearContext();
  }

  @GetMapping("/me")
  public Map<String, Object> me(Authentication auth) {
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autenticado");
    }

    // DNI del usuario (auth.getName())
    String dni = auth.getName();

    // Intentamos enriquecer con nombre y apellido (si existen)
    String nombre = null;
    String apellido = null;
    try {
      var uOpt = usuarioRepo.findByDni(dni);
      if (uOpt.isPresent() && uOpt.get().getPersona() != null) {
        var p = uOpt.get().getPersona();
        nombre = p.getNombre();
        apellido = p.getApellido();
      }
    } catch (Exception ignored) {
      // No rompemos nada si no hay persona
    }

    // Authorities completas (como venías usando)
    var authorities = auth.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)     // p.ej. ROLE_ADMIN
        .collect(Collectors.toList());

    // Roles cortos por si te sirven (ADMIN, SOCIO, etc.)
    var rolesShort = authorities.stream()
        .map(r -> r.replaceFirst("^ROLE_", ""))
        .collect(Collectors.toList());

    // Respuesta compatible + enriquecida
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("name", dni);          // compatibilidad con lo que ya usabas
    out.put("dni", dni);           // explícito
    out.put("nombre", nombre);     // puede ser null
    out.put("apellido", apellido); // puede ser null
    out.put("roles", authorities); // ["ROLE_ADMIN", ...] (igual que antes)
    out.put("rolesShort", rolesShort); // ["ADMIN", ...] (opcional)
    return out;
  }

  @Data @NoArgsConstructor @AllArgsConstructor
  public static class LoginRequest {
    private String dni;
    private String pin;
  }

  @Data @NoArgsConstructor @AllArgsConstructor
  public static class LoginResponse {
    private boolean ok;
    private String dni;
  }
}
