package com.romacontrol.romacontrol_v1.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.model.RolPermiso;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

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
    private final UsuarioRepository usuarioRepo;

    // ============================================================
    // 🔐 LOGIN
    // ============================================================
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        try {

            var usuarioOpt = usuarioRepo.findByDni(req.getDni());
            if (usuarioOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El usuario no existe.");
            }

            var usuario = usuarioOpt.get();

            if (!usuario.isActivo()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Tu cuenta está bloqueada o inactiva.");
            }

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getDni(), req.getPin())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            request.getSession(true);

            securityContextRepository.saveContext(context, request, response);

            return new LoginResponse(true, req.getDni());

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "PIN incorrecto.");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno en el inicio de sesión. Intente nuevamente.");
        }
    }

    // ============================================================
    // 🔓 LOGOUT
    // ============================================================
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

    // ============================================================
    // 👤 USUARIO LOGUEADO
    // ============================================================
    @GetMapping("/me")
    @Transactional(readOnly = true)  // 🔥 NECESARIO PARA EVITAR LazyInitializationException
    public Map<String, Object> me(Authentication auth) {

        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autenticado");
        }

        String dni = auth.getName();
        String nombre = null;
        String apellido = null;

        var uOpt = usuarioRepo.findByDni(dni);
        if (uOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        var usuario = uOpt.get();

            // ============================================
    // 🔥 DEBUG PARA SABER QUÉ PERMISOS ESTÁ VIENDO EL BACKEND
    // ============================================
    System.out.println("\n===============================");
    System.out.println("   DEBUG /me → CARGA DE ROLES");
    System.out.println("===============================");
    System.out.println("Usuario: " + dni);
    System.out.println("Cantidad roles = " + usuario.getRoles().size());

    usuario.getRoles().forEach(r -> {
        System.out.println(" - ROL " + r.getNombre() +
                " → rolPermisos.size = " + r.getRolPermisos().size());
    });
    System.out.println("===============================\n");
    // ============================================

        if (usuario.getPersona() != null) {
            nombre = usuario.getPersona().getNombre();
            apellido = usuario.getPersona().getApellido();
        }

        var authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var rolesShort = authorities.stream()
                .map(r -> r.replaceFirst("^ROLE_", ""))
                .toList();

        // ============================================================
        // 🔥 PERMISOS — nueva lógica con RolPermiso
        // ============================================================
        var permisos = usuario.getRoles().stream()
                .flatMap(rol ->
                        rol.getRolPermisos().stream()
                                .filter(RolPermiso::isHabilitado)
                                .map(RolPermiso::getPermiso)
                                .filter(p -> p != null && Boolean.TRUE.equals(p.isActivo()))
                                .map(p -> p.getRuta())
                )
                .distinct()
                .toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", usuario.getId()); 
        out.put("dni", dni);
        out.put("name", dni);
        out.put("nombre", nombre);
        out.put("apellido", apellido);
        out.put("roles", authorities);
        out.put("rolesShort", rolesShort);
        out.put("permisos", permisos);

        return out;
    }

    // ============================================================
    // 📦 CLASES INTERNAS
    // ============================================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String dni;
        private String pin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private boolean ok;
        private String dni;
    }
}
