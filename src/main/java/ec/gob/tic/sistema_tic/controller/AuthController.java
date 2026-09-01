package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.dto.LoginRequest;
import ec.gob.tic.sistema_tic.dto.LoginResponse;
import ec.gob.tic.sistema_tic.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest datos) {

        return ResponseEntity.ok(authService.login(datos));
    }
}