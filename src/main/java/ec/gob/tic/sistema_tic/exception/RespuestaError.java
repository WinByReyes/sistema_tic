package ec.gob.tic.sistema_tic.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RespuestaError(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        Map<String, String> errores
) {

    public static RespuestaError de(int estado, String error, String mensaje) {
        return new RespuestaError(LocalDateTime.now(), estado, error, mensaje, null);
    }

    public static RespuestaError conErrores(
            int estado,
            String error,
            String mensaje,
            Map<String, String> errores
    ) {
        return new RespuestaError(LocalDateTime.now(), estado, error, mensaje, errores);
    }
}
