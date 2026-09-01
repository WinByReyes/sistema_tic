package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.entity.Auditoria;
import ec.gob.tic.sistema_tic.repository.AuditoriaRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;


    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository =
                auditoriaRepository;
    }


    public void registrar(
            String modulo,
            String accion,
            String descripcion,
            String metodoHttp,
            String ruta,
            String ip) {

        try {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();


            if (
                    authentication == null ||
                            !authentication.isAuthenticated()
            ) {
                return;
            }


            String usuario =
                    authentication.getName();


            String rol =
                    authentication
                            .getAuthorities()
                            .stream()
                            .findFirst()
                            .map(authority ->
                                    authority
                                            .getAuthority()
                                            .replace(
                                                    "ROLE_",
                                                    ""
                                            )
                            )
                            .orElse("DESCONOCIDO");


            Auditoria auditoria =
                    new Auditoria();


            auditoria.setUsuario(usuario);

            auditoria.setRol(rol);

            auditoria.setModulo(modulo);

            auditoria.setAccion(accion);

            auditoria.setDescripcion(
                    descripcion
            );

            auditoria.setMetodoHttp(
                    metodoHttp
            );

            auditoria.setRuta(ruta);

            auditoria.setIp(ip);

            auditoria.setFechaHora(
                    LocalDateTime.now()
            );


            auditoriaRepository.save(
                    auditoria
            );

        } catch (Exception e) {

            /*
             * La auditoría nunca debe impedir
             * que funcione la operación principal.
             */

            System.err.println(
                    "No se pudo registrar auditoría: "
                            + e.getMessage()
            );
        }
    }


    public List<Auditoria> listarUltimas() {

        return auditoriaRepository
                .findTop200ByOrderByFechaHoraDesc();
    }
}