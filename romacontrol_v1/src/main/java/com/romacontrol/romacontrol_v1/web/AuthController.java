// web/AuthController.java
package com.romacontrol.romacontrol_v1.web;

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

  @PostMapping("/login")
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
  }

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
    return Map.of(
        "name", auth.getName(),
        "roles", auth.getAuthorities()
                      .stream()
                      .map(GrantedAuthority::getAuthority)
                      .collect(Collectors.toList())
    );
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
