package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.LoginRequest;
import ec.gob.tic.sistema_tic.dto.LoginResponse;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.exception.CredencialesInvalidasException;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest datos) {

        Usuario usuario = usuarioRepository
                .findByUsuario(datos.getUsuario())
                .orElseThrow(() ->
                        new CredencialesInvalidasException()
                );

        if (!usuario.isEstado()) {
            throw new CredencialesInvalidasException();
        }

        if (!passwordEncoder.matches(
                datos.getContrasena(),
                usuario.getContrasena())) {

            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario.getId(), usuario.getUsuario(), usuario.getRol());
        return new LoginResponse(
                "Inicio de sesión exitoso",
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getUsuario(),
                usuario.getRol()
        );
    }
}
