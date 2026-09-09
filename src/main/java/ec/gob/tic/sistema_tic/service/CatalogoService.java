package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.CatalogoRequestDTO;
import ec.gob.tic.sistema_tic.entity.Catalogo;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.CatalogoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;

    public CatalogoService(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    // ==========================================
    // TIPOS DE CATÁLOGO PERMITIDOS
    // ==========================================

    private static final List<String> TIPOS_PERMITIDOS = List.of(
            // COMPUTADORAS
            "TIPO_EQUIPO",
            "MARCA",
            "TIPO_PROCESADOR",
            "GENERACION_PROCESADOR",
            "RAM",
            "TIPO_DISCO",
            "SISTEMA_OPERATIVO",
            "OFIMATICA",
            "ANTIVIRUS",
            "UBICACION",
            "ESTADO_COMPUTADORA",

            // MANTENIMIENTOS
            "TIPO_MANTENIMIENTO",
            "ESTADO_MANTENIMIENTO",
            "ESTADO_POSTERIOR_COMPUTADORA",
            "RESPONSABLE_MANTENIMIENTO",

            // LEGACY
            "ESTADO"
    );

    // ==========================================
    // VALIDAR TIPO
    // ==========================================

    public String validarTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de catálogo es obligatorio.");
        }

        String tipoNormalizado = tipo.trim().toUpperCase();

        if (!TIPOS_PERMITIDOS.contains(tipoNormalizado)) {
            throw new IllegalArgumentException("Tipo de catálogo no permitido: " + tipo);
        }

        return tipoNormalizado;
    }

    // ==========================================
    // VALIDAR VALOR
    // ==========================================

    @Transactional(readOnly = true)
    public void validarValor(String tipo, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return;
        }
        validarTipo(tipo);
    }

    // ==========================================
    // LISTAR ACTIVOS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Catalogo> listarActivos(String tipo) {
        String tipoValidado = validarTipo(tipo);
        return catalogoRepository.findByTipoAndActivoTrueOrderByNombreAsc(tipoValidado);
    }

    // ==========================================
    // LISTAR TODOS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Catalogo> listarTodos(String tipo) {
        String tipoValidado = validarTipo(tipo);
        return catalogoRepository.findByTipoOrderByNombreAsc(tipoValidado);
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public Catalogo buscarPorId(Long id) {
        return catalogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Catálogo no encontrado con ID: " + id
                ));
    }

    // ==========================================
    // CREAR
    // ==========================================

    @Transactional
    public Catalogo crear(CatalogoRequestDTO datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos del catálogo son obligatorios.");
        }

        String tipo = validarTipo(datos.getTipo());

        if (datos.getNombre() == null || datos.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del catálogo es obligatorio.");
        }

        String nombre = datos.getNombre().trim();

        if (catalogoRepository.findByTipoAndNombreIgnoreCase(tipo, nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe este valor en el catálogo: " + nombre);
        }

        Catalogo catalogo = new Catalogo();
        catalogo.setTipo(tipo);
        catalogo.setNombre(nombre);
        catalogo.setActivo(datos.getActivo() == null ? true : datos.getActivo());

        return catalogoRepository.save(catalogo);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @Transactional
    public Catalogo actualizar(Long id, CatalogoRequestDTO datos) {
        Catalogo catalogo = buscarPorId(id);

        if (datos == null) {
            throw new IllegalArgumentException("Los datos del catálogo son obligatorios.");
        }

        String tipo = validarTipo(datos.getTipo());

        if (datos.getNombre() == null || datos.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del catálogo es obligatorio.");
        }

        String nombre = datos.getNombre().trim();

        catalogoRepository.findByTipoAndNombreIgnoreCase(tipo, nombre).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe este valor en el catálogo.");
            }
        });

        catalogo.setTipo(tipo);
        catalogo.setNombre(nombre);

        if (datos.getActivo() != null) {
            catalogo.setActivo(datos.getActivo());
        }

        return catalogoRepository.save(catalogo);
    }

    // ==========================================
    // DESACTIVAR
    // ==========================================

    @Transactional
    public void desactivar(Long id) {
        Catalogo catalogo = buscarPorId(id);
        catalogo.setActivo(false);
        catalogoRepository.save(catalogo);
    }

    // ==========================================
    // ACTIVAR
    // ==========================================

    @Transactional
    public void activar(Long id) {
        Catalogo catalogo = buscarPorId(id);
        catalogo.setActivo(true);
        catalogoRepository.save(catalogo);
    }
}
