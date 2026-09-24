package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.InventarioItemDTO;
import ec.gob.tic.sistema_tic.dto.InventarioResumenDTO;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.EquipoTecnologico;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.entity.Impresora;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.EquipoTecnologicoRepository;
import ec.gob.tic.sistema_tic.repository.ImpresoraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Permite obtener el resumen general del inventario del sistema,
 * opcionalmente filtrado por año y/o rango de fechas de registro.
 */
@Service
public class InventarioService {

    /**
     * Límites "sin filtro" dentro del rango aceptado por PostgreSQL.
     * Nunca se envían parámetros NULL a la consulta para evitar que el
     * driver no pueda inferir el tipo de los parámetros de comparación.
     */
    private static final LocalDateTime LIMITE_INICIAL = LocalDateTime.of(1, 1, 1, 0, 0);
    private static final LocalDateTime LIMITE_FINAL = LocalDateTime.of(9999, 12, 31, 0, 0);

    private final ComputadoraRepository computadoraRepository;
    private final EquipoTecnologicoRepository equipoTecnologicoRepository;
    private final ImpresoraRepository impresoraRepository;

    public InventarioService(
            ComputadoraRepository computadoraRepository,
            EquipoTecnologicoRepository equipoTecnologicoRepository,
            ImpresoraRepository impresoraRepository) {
        this.computadoraRepository = computadoraRepository;
        this.equipoTecnologicoRepository = equipoTecnologicoRepository;
        this.impresoraRepository = impresoraRepository;
    }

    // =========================================================
    // INVENTARIO GENERAL (TABLA CONSOLIDADA)
    // =========================================================

    /**
     * Lista consolidada y ordenada alfabéticamente con las computadoras,
     * los periféricos (equipos tecnológicos) y las impresoras registradas.
     *
     * <p>El orden es alfabético por tipo de módulo de origen
     * (Computadora &lt; Impresora &lt; Periférico) y luego por marca,
     * modelo y serie.</p>
     */
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarInventarioGeneral() {
        List<InventarioItemDTO> items = new ArrayList<>();

        computadoraRepository.findAllWithFuncionario().forEach(c ->
                items.add(itemComputadora(c)));
        equipoTecnologicoRepository.findAllWithFuncionario().forEach(e ->
                items.add(itemPeriferico(e)));
        impresoraRepository.findAllWithFuncionario().forEach(i ->
                items.add(itemImpresora(i)));

        items.sort(Comparator
                .comparing((InventarioItemDTO it) -> textoOrdenable(it.getTipo()))
                .thenComparing(it -> textoOrdenable(it.getMarca()))
                .thenComparing(it -> textoOrdenable(it.getModelo()))
                .thenComparing(it -> textoOrdenable(it.getSerie())));

        return items;
    }

    private InventarioItemDTO itemComputadora(Computadora c) {
        InventarioItemDTO item = new InventarioItemDTO();
        item.setTipo("Computadora");
        cargarDatosComunes(item, c.getFuncionario(), c.getTipo(), c.getMarca(), c.getModelo(), c.getSerie(), c.getEstado());
        return item;
    }

    private InventarioItemDTO itemPeriferico(EquipoTecnologico e) {
        InventarioItemDTO item = new InventarioItemDTO();
        item.setTipo("Periférico");
        cargarDatosComunes(item, e.getFuncionario(), e.getTipoEquipo(), e.getMarca(), e.getModelo(), e.getSerie(), e.getEstado());
        return item;
    }

    private InventarioItemDTO itemImpresora(Impresora i) {
        InventarioItemDTO item = new InventarioItemDTO();
        item.setTipo("Impresora");
        cargarDatosComunes(item, i.getFuncionario(), i.getTipoEquipo(), i.getMarca(), i.getModelo(), i.getSerie(), i.getEstado());
        return item;
    }

    private void cargarDatosComunes(InventarioItemDTO item, Funcionario funcionario,
                                    String tipoEquipo, String marca, String modelo,
                                    String serie, String estado) {
        if (funcionario != null) {
            item.setFuncionario(funcionario.getNombreCompleto());
            item.setUnidadAdministrativa(funcionario.getUnidadAdministrativa());
        }
        item.setTipoEquipo(tipoEquipo);
        item.setMarca(marca);
        item.setModelo(modelo);
        item.setSerie(serie);
        item.setEstado(estado);
    }

    private String textoOrdenable(String valor) {
        return valor == null ? "" : valor.toLowerCase();
    }

    @Transactional(readOnly = true)
    public InventarioResumenDTO obtenerResumen() {
        return obtenerResumen(null, null, null);
    }

    @Transactional(readOnly = true)
    public InventarioResumenDTO obtenerResumen(Integer anio, LocalDate desde, LocalDate hasta) {
        InventarioResumenDTO resumen = new InventarioResumenDTO();

        LocalDateTime inicio = LIMITE_INICIAL;
        LocalDateTime finExclusivo = LIMITE_FINAL;

        if (anio != null) {
            inicio = LocalDate.of(anio, 1, 1).atStartOfDay();
            finExclusivo = diaSiguiente(LocalDate.of(anio, 12, 31));
        }

        if (desde != null) {
            LocalDateTime inicioDesde = desde.atStartOfDay();
            if (inicioDesde.isAfter(inicio)) {
                inicio = inicioDesde;
            }
        }

        if (hasta != null) {
            LocalDateTime finHasta = diaSiguiente(hasta);
            if (finHasta.isBefore(finExclusivo)) {
                finExclusivo = finHasta;
            }
        }

        if (!inicio.isBefore(finExclusivo)) {
            resumen.setComputadorasPorTipo(java.util.List.of());
            resumen.setEquipamientoPorTipo(java.util.List.of());
            resumen.setTotalComputadoras(0L);
            resumen.setTotalEquipamiento(0L);
            resumen.setTotalGeneral(0L);
        } else {
            resumen.setComputadorasPorTipo(computadoraRepository.resumenPorTipoEntreFechas(inicio, finExclusivo));
            resumen.setEquipamientoPorTipo(equipoTecnologicoRepository.resumenPorTipoEntreFechas(inicio, finExclusivo));

            long totalComputadoras = resumen.getComputadorasPorTipo().stream()
                    .mapToLong(dto -> dto.getCantidad() != null ? dto.getCantidad() : 0L)
                    .sum();
            long totalEquipamiento = resumen.getEquipamientoPorTipo().stream()
                    .mapToLong(dto -> dto.getCantidad() != null ? dto.getCantidad() : 0L)
                    .sum();

            resumen.setTotalComputadoras(totalComputadoras);
            resumen.setTotalEquipamiento(totalEquipamiento);
            resumen.setTotalGeneral(totalComputadoras + totalEquipamiento);
        }

        resumen.setAnio(anio);
        resumen.setDesde(desde != null ? desde.toString() : null);
        resumen.setHasta(hasta != null ? hasta.toString() : null);

        return resumen;
    }

    private LocalDateTime diaSiguiente(LocalDate dia) {
        try {
            return dia.plusDays(1).atStartOfDay();
        } catch (DateTimeException ex) {
            return LIMITE_FINAL;
        }
    }
}