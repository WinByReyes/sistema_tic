package ec.gob.tic.sistema_tic.dto;

/**
 * Ítem de la tabla consolidada del inventario general.
 *
 * <p>Agrega en una sola lista las computadoras, los periféricos
 * (equipos tecnológicos) y las impresoras registradas en el sistema.
 * La columna {@code tipo} indica el módulo de origen y permite ordenar
 * alfabéticamente la tabla.</p>
 */
public class InventarioItemDTO {

    private String tipo;
    private String funcionario;
    private String unidadAdministrativa;
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String serie;
    private String estado;

    public InventarioItemDTO() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    public void setUnidadAdministrativa(String unidadAdministrativa) {
        this.unidadAdministrativa = unidadAdministrativa;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}