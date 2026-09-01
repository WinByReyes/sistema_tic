package ec.gob.tic.sistema_tic.security;

import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import ec.gob.tic.sistema_tic.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // No existe Authorization
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        // Token inválido o expirado
        if (!jwtService.validarToken(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String username =
                    jwtService.obtenerUsuario(token);

            // Verificar existencia y estado activo del usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(username);

            if (usuarioOpt.isPresent() && Boolean.TRUE.equals(usuarioOpt.get().isEstado())) {

                String rol =
                        jwtService.obtenerRol(token);

                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + rol);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(authority)
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
