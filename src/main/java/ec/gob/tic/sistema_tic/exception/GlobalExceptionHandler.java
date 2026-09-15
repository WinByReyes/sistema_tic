package ec.gob.tic.sistema_tic.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<RespuestaError> manejarCredencialesInvalidas(
            CredencialesInvalidasException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaError> manejarDatoDuplicado(
            DataIntegrityViolationException ex) {
        LOGGER.error("Violación de integridad de datos", ex);

        Throwable raiz = ex.getRootCause();
        String detalle = raiz != null ? raiz.getMessage() : null;
        String mensaje = "El dato que intenta registrar ya existe o viola una restricción de la base de datos.";

        if (detalle != null) {
            String min = detalle.toLowerCase();
            if (min.contains("check constraint")) {
                mensaje = "Los valores ingresados no cumplen una regla de validación de la base de datos.";
            } else if (min.contains("duplicate key") || min.contains("unique constraint")) {
                mensaje = "El dato que intenta registrar ya existe en la base de datos.";
            } else if (min.contains("not null")) {
                mensaje = "Falta un dato obligatorio para registrar la información.";
            } else if (min.contains("foreign key")) {
                mensaje = "El registro está relacionado con otro dato que impide la operación.";
            } else if (min.contains("null value")) {
                mensaje = "Falta un dato obligatorio para registrar la información.";
            }
        }

        return respuesta(HttpStatus.CONFLICT, "Conflicto de datos", mensaje);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = error instanceof FieldError fieldError
                    ? fieldError.getField() : error.getObjectName();
            errores.put(campo, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(RespuestaError.conErrores(
                HttpStatus.BAD_REQUEST.value(), "Datos inválidos",
                "Revise los campos marcados.", errores));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespuestaError> manejarConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errores.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ResponseEntity.badRequest().body(RespuestaError.conErrores(
                HttpStatus.BAD_REQUEST.value(), "Datos inválidos",
                "Revise las restricciones violadas.", errores));
    }

    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<RespuestaError> manejarSolicitudInvalida(Exception ex) {
        String mensaje = ex instanceof IllegalArgumentException && ex.getMessage() != null
                ? ex.getMessage() : "La solicitud contiene datos inválidos o incompletos.";
        return respuesta(HttpStatus.BAD_REQUEST, "Solicitud inválida", mensaje);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RespuestaError> manejarMetodoNoCompatible(HttpRequestMethodNotSupportedException ex) {
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "Operación no permitida",
                "El método de la solicitud no es compatible con este recurso.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RespuestaError> manejarFormatoNoCompatible(HttpMediaTypeNotSupportedException ex) {
        return respuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Formato no compatible",
                "El formato enviado no es compatible con este recurso.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> manejarRutaNoEncontrada(NoResourceFoundException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", "La ruta solicitada no existe.");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RespuestaError> manejarEstadoInvalido(IllegalStateException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "La operación no se puede realizar en el estado actual.";
        return respuesta(HttpStatus.CONFLICT, "Operación no permitida", mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarErrorInesperado(Exception ex) {
        LOGGER.error("Error no controlado al procesar una solicitud", ex);

        String causa = ex.getMessage();
        Throwable raiz = ex.getCause() != null ? ex.getCause() : ex;
        if (raiz != raiz.getCause() && raiz.getCause() != null) {
            causa = raiz.getCause().getMessage() != null
                    ? raiz.getCause().getMessage() : causa;
        }
        if (causa == null || causa.isBlank()) {
            causa = ex.getClass().getSimpleName();
        }

        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado. Detalle: " + causa);
    }

    private ResponseEntity<RespuestaError> respuesta(HttpStatus estado, String error, String mensaje) {
        return ResponseEntity.status(estado)
                .body(RespuestaError.de(estado.value(), error, mensaje));
    }
}
