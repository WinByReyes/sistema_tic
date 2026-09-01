package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.ConfiguracionInstitucional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfiguracionInstitucionalDTO {

    private Long id;

    @NotBlank(message = "El nombre de la institución es obligatorio")
    @Size(max = 200, message = "El nombre de la institución no puede superar los 200 caracteres")
    private String nombreInstitucion;

    @Size(max = 200, message = "La unidad administrativa no puede superar los 200 caracteres")
    private String unidadAdministrativa;

    @Size(max = 250, message = "La dirección no puede superar los 250 caracteres")
    private String direccion;

    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String telefono;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    public ConfiguracionInstitucionalDTO() {
    }

    public ConfiguracionInstitucionalDTO(ConfiguracionInstitucional entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.nombreInstitucion = entity.getNombreInstitucion();
            this.unidadAdministrativa = entity.getUnidadAdministrativa();
            this.direccion = entity.getDireccion();
            this.telefono = entity.getTelefono();
            this.correo = entity.getCorreo();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    public void setUnidadAdministrativa(String unidadAdministrativa) {
        this.unidadAdministrativa = unidadAdministrativa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
