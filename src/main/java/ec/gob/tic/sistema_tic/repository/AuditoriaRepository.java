package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {

    List<Auditoria>
    findTop200ByOrderByFechaHoraDesc();
}