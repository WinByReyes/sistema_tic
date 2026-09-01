package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "catalogos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_catalogo_tipo_nombre",
                        columnNames = {"tipo", "nombre"}
                )
        }
)
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Ejemplos:
     *
     * MARCA
     * TIPO_PROCESADOR
     * GENERACION_PROCESADOR
     * RAM
     * TIPO_DISCO
     * SISTEMA_OPERATIVO
     * ESTADO
     */

    @NotBlank(message = "El tipo de catálogo es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String tipo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;


    public Catalogo() {

        this.activo = true;

        this.fechaCreacion =
                LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion) {

        this.fechaCreacion =
                fechaCreacion;
    }
}