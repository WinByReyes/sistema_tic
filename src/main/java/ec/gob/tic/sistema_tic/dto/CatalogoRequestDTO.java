package ec.gob.tic.sistema_tic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CatalogoRequestDTO {

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 50)
    private String tipo;


    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;


    private Boolean activo;


    public CatalogoRequestDTO() {
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
}