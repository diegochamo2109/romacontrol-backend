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

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  // ============================================================
  // 🔥 FILTER CHAIN
  // ============================================================
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))   // << CORS ACTIVADO
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/api/auth/login",
                "/api/auth/logout"
            ).permitAll()
            .requestMatchers("/api/tipo-gasto/**", "/api/gastos/**").permitAll()
            .requestMatchers("/api/terminal/**").hasAnyRole("ADMIN", "PROFESOR")
            .requestMatchers("/api/asistencias/**").permitAll()
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

  // ============================================================
  // 🔥 CORS CONFIG FINAL PARA NETLIFY + RENDER
  // ============================================================
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

      CorsConfiguration config = new CorsConfiguration();

      config.setAllowedOrigins(Arrays.asList(
          "http://127.0.0.1:5500",
          "http://localhost:5500",
          "https://voluble-kulfi-9c5ef1.netlify.app",               // << FRONTEND
          "https://romacontrol-backend-n3p6.onrender.com"            // << BACKEND
      ));

      config.setAllowedMethods(Arrays.asList(
          "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
      ));

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
