package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 150, message = "El usuario no puede superar los 150 caracteres")
    @Column(nullable = false, unique = true, length = 150)
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 150, message = "La contraseña debe tener entre 6 y 150 caracteres")
    @Column(name = "contrasena", nullable = false, length = 150)
    private String contrasena;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "ADMIN|TECNICO",
            message = "El rol debe ser ADMIN o TECNICO"
    )
    @Column(nullable = false, length = 30)
    private String rol;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;


    public Usuario()
    {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean isEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}