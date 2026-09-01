package ec.gob.tic.sistema_tic.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoUtil {

    private TextoUtil() {
    }

    /**
     * Limpia espacios innecesarios.
     */
    public static String limpiar(String texto) {

        if (texto == null) {
            return null;
        }

        return texto
                .trim()
                .replaceAll("\\s+", " ");
    }


    /**
     * Convierte texto a formato tipo título.
     *
     * Ejemplo:
     *
     * "   JUAN   PEREZ "
     *
     * -> "Juan Perez"
     */
    public static String formatoNombre(String texto) {

        texto = limpiar(texto);

        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        String[] palabras =
                texto.toLowerCase(Locale.ROOT)
                        .split(" ");

        StringBuilder resultado =
                new StringBuilder();

        for (String palabra : palabras) {

            if (palabra.isEmpty()) {
                continue;
            }

            resultado.append(
                    Character.toUpperCase(
                            palabra.charAt(0)
                    )
            );

            if (palabra.length() > 1) {

                resultado.append(
                        palabra.substring(1)
                );

            }

            resultado.append(" ");
        }

        return resultado
                .toString()
                .trim();
    }


    /**
     * Convierte valores que no necesitan
     * formato de nombre.
     *
     * Ejemplo:
     *
     * "   windows 11 "
     *
     * -> "Windows 11"
     */
    public static String formatoTexto(String texto) {

        texto = limpiar(texto);

        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        return texto.substring(0, 1)
                .toUpperCase(Locale.ROOT)
                + texto.substring(1);
    }


    /**
     * Normaliza un valor para comparaciones.
     */
    public static String normalizarComparacion(
            String texto) {

        if (texto == null) {
            return null;
        }

        String normalizado =
                Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                );

        return normalizado
                .replaceAll(
                        "\\p{M}",
                        ""
                )
                .trim()
                .toLowerCase(Locale.ROOT);
    }


    /**
     * Limpia una cédula.
     */
    public static String limpiarCedula(
            String cedula) {

        if (cedula == null) {
            return null;
        }

        return cedula
                .replaceAll("\\D", "")
                .trim();
    }
}