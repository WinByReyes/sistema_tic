package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "usuario",
            nullable = false,
            length = 150
    )
    private String usuario;


    @Column(
            name = "rol",
            nullable = false,
            length = 30
    )
    private String rol;


    @Column(
            name = "modulo",
            nullable = false,
            length = 50
    )
    private String modulo;


    @Column(
            name = "accion",
            nullable = false,
            length = 50
    )
    private String accion;


    @Column(
            name = "descripcion",
            nullable = false,
            length = 255
    )
    private String descripcion;


    @Column(
            name = "metodo_http",
            nullable = false,
            length = 10
    )
    private String metodoHttp;


    @Column(
            name = "ruta",
            nullable = false,
            length = 255
    )
    private String ruta;


    @Column(
            name = "ip",
            length = 45
    )
    private String ip;


    @Column(
            name = "fecha_hora",
            nullable = false
    )
    private LocalDateTime fechaHora;


    public Auditoria() {
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


    public String getRol() {
        return rol;
    }


    public void setRol(String rol) {
        this.rol = rol;
    }


    public String getModulo() {
        return modulo;
    }


    public void setModulo(String modulo) {
        this.modulo = modulo;
    }


    public String getAccion() {
        return accion;
    }


    public void setAccion(String accion) {
        this.accion = accion;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getMetodoHttp() {
        return metodoHttp;
    }


    public void setMetodoHttp(String metodoHttp) {
        this.metodoHttp = metodoHttp;
    }


    public String getRuta() {
        return ruta;
    }


    public void setRuta(String ruta) {
        this.ruta = ruta;
    }


    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    public LocalDateTime getFechaHora() {
        return fechaHora;
    }


    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}