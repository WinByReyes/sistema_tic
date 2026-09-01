package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_institucional")
public class ConfiguracionInstitucional {

    @Id
    private Long id;


    @Column(
            name = "nombre_institucion",
            nullable = false,
            length = 200
    )
    private String nombreInstitucion;


    @Column(
            name = "unidad_administrativa",
            length = 200
    )
    private String unidadAdministrativa;


    @Column(
            name = "direccion",
            length = 250
    )
    private String direccion;


    @Column(
            name = "telefono",
            length = 30
    )
    private String telefono;


    @Column(
            name = "correo",
            length = 150
    )
    private String correo;


    public ConfiguracionInstitucional() {
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


    public void setNombreInstitucion(
            String nombreInstitucion) {

        this.nombreInstitucion =
                nombreInstitucion;
    }


    public String getUnidadAdministrativa() {
        return unidadAdministrativa;
    }


    public void setUnidadAdministrativa(
            String unidadAdministrativa) {

        this.unidadAdministrativa =
                unidadAdministrativa;
    }


    public String getDireccion() {
        return direccion;
    }


    public void setDireccion(
            String direccion) {

        this.direccion =
                direccion;
    }


    public String getTelefono() {
        return telefono;
    }


    public void setTelefono(
            String telefono) {

        this.telefono =
                telefono;
    }


    public String getCorreo() {
        return correo;
    }


    public void setCorreo(
            String correo) {

        this.correo =
                correo;
    }
}