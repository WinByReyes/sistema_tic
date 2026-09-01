package ec.gob.tic.sistema_tic.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long tiempoExpiracion = 1000 * 60 * 60; // 1 hora

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generarToken(
            Long id,
            String usuario,
            String rol) {

        Date ahora = new Date();
        Date expiracion = new Date(
                ahora.getTime() + tiempoExpiracion
        );

        return Jwts.builder()
                .subject(usuario)
                .claim("id", id)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    public String obtenerUsuario(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String obtenerRol(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("rol", String.class);
    }

    public boolean validarToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}