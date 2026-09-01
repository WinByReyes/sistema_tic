package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Usuario;

public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String usuario;
    private String rol;
    private Boolean estado;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.usuario = usuario.getUsuario();
        this.rol = usuario.getRol();
        this.estado = usuario.isEstado();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    public Boolean getEstado() {
        return estado;
    }
}