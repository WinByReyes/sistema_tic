package ec.gob.tic.sistema_tic.dto;

import java.time.LocalDateTime;

public class BackupResponseDTO {

    private String nombreArchivo;

    private Long tamanioBytes;

    private LocalDateTime fechaCreacion;


    public BackupResponseDTO() {
    }


    public BackupResponseDTO(
            String nombreArchivo,
            Long tamanioBytes,
            LocalDateTime fechaCreacion) {

        this.nombreArchivo =
                nombreArchivo;

        this.tamanioBytes =
                tamanioBytes;

        this.fechaCreacion =
                fechaCreacion;
    }


    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }


    public Long getTamanioBytes() {
        return tamanioBytes;
    }

    public void setTamanioBytes(Long tamanioBytes) {
        this.tamanioBytes = tamanioBytes;
    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}