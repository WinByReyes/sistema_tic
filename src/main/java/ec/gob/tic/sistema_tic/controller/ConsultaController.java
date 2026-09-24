package ec.gob.tic.sistema_tic.controller;


import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.ConsultaComputadoraResponseDTO;
import ec.gob.tic.sistema_tic.dto.ConsultaFuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.dto.EquipoTecnologicoResponseDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.service.ConsultaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {


    private final ConsultaService consultaService;


    public ConsultaController(
            ConsultaService consultaService) {

        this.consultaService =
                consultaService;

    }


    // ========================================
    // CONSULTAR FUNCIONARIO
    // ========================================
    @Auditable(
            modulo = "CONSULTAS",
            accion = "CONSULTAR_FUNCIONARIO",
            descripcion = "Consultó información de un funcionario"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")

    @GetMapping("/funcionario/{cedula}")

    public ResponseEntity<
            ConsultaFuncionarioResponseDTO>
    consultarFuncionario(

            @PathVariable String cedula) {


        return ResponseEntity.ok(

                consultaService
                        .consultarPorCedula(cedula)

        );

    }


    // ========================================
    // CONSULTAR COMPUTADORA
    // ========================================
    @Auditable(
            modulo = "CONSULTAS",
            accion = "CONSULTAR_COMPUTADORAS",
            descripcion = "Consultó información de computadoras"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")

    @GetMapping("/computadoras")

    public ResponseEntity<
            List<ConsultaComputadoraResponseDTO>>
    buscarComputadoras(

            @RequestParam(
                    required = false)
            String nombre,

            @RequestParam(
                    required = false)
            String serie,

            @RequestParam(
                    required = false)
            String cedula) {


        return ResponseEntity.ok(

                consultaService
                        .consultarComputadoras(
                                nombre,
                                serie,
                                cedula
                        )

        );

    }


    // ========================================
    // CONSULTAR EQUIPAMIENTO TECNOLÓGICO
    // ========================================
    @Auditable(
            modulo = "CONSULTAS",
            accion = "CONSULTAR_EQUIPOS_TECNOLOGICOS",
            descripcion = "Consultó información de equipamiento tecnológico"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")

    @GetMapping("/equipos-tecnologicos")

    public ResponseEntity<
            List<EquipoTecnologicoResponseDTO>>
    buscarEquiposTecnologicos(

            @RequestParam(
                    required = false)
            String serie,

            @RequestParam(
                    required = false)
            String cedula) {


        return ResponseEntity.ok(

                consultaService
                        .consultarEquiposTecnologicos(
                                serie,
                                cedula
                        )

        );

    }


    // ========================================
    // CONSULTAR MANTENIMIENTOS
    // ========================================
    @Auditable(
            modulo = "CONSULTAS",
            accion = "CONSULTAR_MANTENIMIENTOS",
            descripcion = "Consultó mantenimientos por fecha"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")

    @GetMapping("/mantenimientos")

    public ResponseEntity<
            List<MantenimientoResponseDTO>>
    buscarMantenimientosPorFecha(

            @RequestParam

            @DateTimeFormat(
                    iso =
                            DateTimeFormat.ISO.DATE)

            LocalDate desde,


            @RequestParam

            @DateTimeFormat(
                    iso =
                            DateTimeFormat.ISO.DATE)

            LocalDate hasta) {


        LocalDateTime inicio =
                desde.atStartOfDay();


        LocalDateTime fin =
                hasta
                        .plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);


        return ResponseEntity.ok(

                consultaService
                        .buscarMantenimientosPorFecha(
                                inicio,
                                fin
                        )

        );

    }

}