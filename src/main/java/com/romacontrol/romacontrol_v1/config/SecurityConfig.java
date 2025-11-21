package com.romacontrol.romacontrol_v1.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  // ===========================
  // 🔐 Encoder
  // ===========================
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  // ===========================
  // 🔐 Provider
  // ===========================
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  // ===========================
  // 🔐 AuthenticationManager
  // ===========================
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  // ===========================
  // 🔐 SecurityContext (Sesión)
  // ===========================
  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  // ===========================
  // 🔐 Cadena de filtros
  // ===========================
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // 1️⃣ Endpoints liberados
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/api/auth/login",
                "/api/auth/logout"   // 🔥 agregado
            ).permitAll()

            // 2️⃣ Endpoints públicos del sistema
            .requestMatchers("/api/tipo-gasto/**", "/api/gastos/**").permitAll()

            // 3️⃣ Permisos por rol
            .requestMatchers("/api/terminal/**").hasAnyRole("ADMIN", "PROFESOR")
            .requestMatchers("/api/asistencias/**").permitAll()

            // 4️⃣ Todo lo demás requiere login (JSESSIONID)
            .anyRequest().authenticated()
        )
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .securityContext(sc ->
            sc.securityContextRepository(securityContextRepository())
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }

// ===========================
// 🔐 CORS para el frontend
// ===========================
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.asList(
        "http://127.0.0.1:5500",
        "http://localhost:5500"
    ));
    config.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));
    
    // 🔥🔥 AGREGADO: permitir header 'dni'
    config.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "dni",
        "X-Requested-With",
        "Accept"
    ));

    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

}
