package ec.gob.tic.sistema_tic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.gob.tic.sistema_tic.exception.RespuestaError;
import ec.gob.tic.sistema_tic.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.MediaType;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ObjectMapper objectMapper) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) ->
                                escribirError(response, 401, "No autorizado",
                                        "Debe iniciar sesión o su sesión ha expirado."))
                        .accessDeniedHandler((request, response, ex) ->
                                escribirError(response, 403, "Acceso denegado",
                                        "No tiene permisos para realizar esta operación."))
                )

                .authorizeHttpRequests(auth -> auth

                        // ==========================
                        // API DE LOGIN
                        // ==========================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // ==========================
                        // PÁGINAS DEL FRONTEND
                        // ==========================

.requestMatchers(
                                "/",
                                "/login",
                                "/dashboard",
                                "/usuarios",
                                "/funcionarios",
                                "/computadoras",
                                "/equipos-tecnologicos",
                                "/impresoras",
                                "/inventario",
                                "/mantenimientos",
                                "/consultas",
                                "/administracion",
                                "/catalogos",
                                "/auditoria",
                                "/respaldos",
                                "/configuracion-institucional"

                        ).permitAll()


                        // ==========================
                        // ARCHIVOS ESTÁTICOS
                        // ==========================

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()


                        // ==========================
                        // TODO LO DEMÁS
                        // ==========================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private void escribirError(
            jakarta.servlet.http.HttpServletResponse response,
            int estado,
            String error,
            String mensaje
    ) throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(),
                RespuestaError.de(estado, error, mensaje));
    }
}
