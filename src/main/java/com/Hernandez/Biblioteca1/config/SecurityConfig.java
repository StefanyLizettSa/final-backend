package com.Hernandez.Biblioteca1.config;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.Hernandez.Biblioteca1.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // ✅ CSRF deshabilitado
            .csrf(AbstractHttpConfigurer::disable)
            // ✅ FORM LOGIN deshabilitado
            .formLogin(AbstractHttpConfigurer::disable)
            // ✅ LOGOUT deshabilitado
            .logout(AbstractHttpConfigurer::disable)
            // ✅ HTTP BASIC HABILITADO
            .httpBasic(httpBasic -> {
                httpBasic.realmName("Biblioteca API");
            })
            // ✅ AUTORIZACIONES
            .authorizeHttpRequests(auth -> auth
                // ==========================================
                // 1. ENDPOINTS PÚBLICOS
                // ==========================================
                .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // ==========================================
                // 2. LIBROS - GET PÚBLICO
                // ==========================================
                .requestMatchers(HttpMethod.GET, "/api/libros").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/libros/**").permitAll()

                // ==========================================
                // 3. LIBROS - POST, PUT, DELETE con roles
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/api/libros")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/libros")
                    .hasRole("BIBLIOTECARIO")
                .requestMatchers(HttpMethod.PUT, "/api/libros/**")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.DELETE, "/api/libros/**")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                // ==========================================
                // 4. CATEGORÍAS - Solo ADMIN
                // ==========================================
                .requestMatchers("/api/categorias/**").hasRole("ADMIN")

                // ==========================================
                // 5. EDITORIALES - Solo ADMIN
                // ==========================================
                .requestMatchers("/api/editoriales/**").hasRole("ADMIN")

                // ==========================================
                // 6. PRÉSTAMOS
                // ==========================================
                .requestMatchers(HttpMethod.GET, "/api/prestamos")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.GET, "/api/prestamos/usuario/**")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.GET, "/api/prestamos/activos")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.POST, "/api/prestamos")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO", "ESTUDIANTE")
                .requestMatchers(HttpMethod.PUT, "/api/prestamos/*/devolver")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                // ==========================================
                // 7. USUARIOS - ADMIN y BIBLIOTECARIO
                // ==========================================
                .requestMatchers(HttpMethod.GET, "/api/usuarios")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**")
                    .hasAnyRole("ADMIN", "BIBLIOTECARIO")

                // ==========================================
                // 8. CUALQUIER OTRA COSA
                // ==========================================
                .anyRequest().authenticated()
            )
            // ✅ SESIÓN STATELESS
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // ✅ MANEJO DE EXCEPCIONES
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"estado\":401,\"error\":\"No autenticado\"," +
                        "\"mensaje\":\"Se requiere autenticación. Usa usuario y contraseña en el header Authorization: Basic ...\"}"
                    );
                })
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"estado\":403,\"error\":\"Acceso denegado\"," +
                        "\"mensaje\":\"No tienes permisos para acceder a este recurso\"}"
                    );
                })
            )
            // ✅ PROVIDER DE AUTENTICACIÓN
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://final-frontend-green.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
