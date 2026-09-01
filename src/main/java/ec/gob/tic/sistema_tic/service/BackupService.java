package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.BackupResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


@Service
public class BackupService {

    @Value("${backup.enabled:true}")
    private boolean enabled;


    @Value("${backup.directory:backups}")
    private String backupDirectory;


    @Value("${backup.pg-dump-path:pg_dump}")
    private String pgDumpPath;


    @Value("${backup.database.host:localhost}")
    private String host;


    @Value("${backup.database.port:5432}")
    private String port;


    @Value("${backup.database.name:sistema_tics}")
    private String database;


    @Value("${backup.database.username:postgres}")
    private String username;


    @Value("${backup.database.password:}")
    private String password;


    public String crearRespaldo()
            throws IOException, InterruptedException {

        if (!enabled) {

            throw new IllegalStateException(
                    "Los respaldos están deshabilitados."
            );
        }


        Path directorio =
                Paths.get(backupDirectory)
                        .toAbsolutePath();


        Files.createDirectories(
                directorio
        );


        String fecha =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd_HH-mm-ss"
                                )
                        );


        Path archivo =
                directorio.resolve(
                        "sistema_tics_"
                                + fecha
                                + ".sql"
                );


        ProcessBuilder processBuilder =
                new ProcessBuilder(

                        pgDumpPath,

                        "-h",
                        host,

                        "-p",
                        port,

                        "-U",
                        username,

                        "-d",
                        database,

                        "-F",
                        "p",

                        "-f",
                        archivo.toString()
                );


        /*
         * La contraseña se entrega mediante
         * variable de entorno para evitar
         * colocarla en los argumentos del proceso.
         */

        processBuilder
                .environment()
                .put(
                        "PGPASSWORD",
                        password
                );


        processBuilder
                .redirectErrorStream(true);


        Process proceso =
                processBuilder.start();


        int resultado =
                proceso.waitFor();


        if (resultado != 0) {

            try {

                Files.deleteIfExists(
                        archivo
                );

            } catch (IOException ignored) {
            }


            throw new IllegalStateException(
                    "pg_dump no pudo crear el respaldo."
            );
        }


        if (!Files.exists(archivo)) {

            throw new IllegalStateException(
                    "El archivo de respaldo no fue creado."
            );
        }


        return archivo.toString();
    }


    public List<BackupResponseDTO> listarRespaldos()
            throws IOException {

        Path directorio =
                Paths.get(backupDirectory)
                        .toAbsolutePath();


        if (!Files.exists(directorio)) {

            return List.of();
        }


        try (Stream<Path> archivos = Files.list(directorio)) {

            return archivos

                    .filter(archivo ->
                            archivo.toString()
                                    .endsWith(".sql")
                    )

                    .map(this::mapearArchivo)

                    .sorted(
                            Comparator.comparing(
                                            BackupResponseDTO::getFechaCreacion
                                    )
                                    .reversed()
                    )

                    .toList();
        }
    }


    private BackupResponseDTO mapearArchivo(
            Path archivo) {

        try {

            long tamanio =
                    Files.size(archivo);


            FileTime fechaModificacion =
                    Files.getLastModifiedTime(archivo);


            LocalDateTime fecha =
                    LocalDateTime.ofInstant(
                            fechaModificacion.toInstant(),
                            ZoneId.systemDefault()
                    );


            return new BackupResponseDTO(
                    archivo.getFileName().toString(),
                    tamanio,
                    fecha
            );

        } catch (IOException e) {

            return new BackupResponseDTO(
                    archivo.getFileName().toString(),
                    0L,
                    null
            );
        }
    }
}