package ec.gob.tic.sistema_tic.exception;

/**
 * Se usa para no exponer si un nombre de usuario existe o si la cuenta está activa.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos.");
    }
}
