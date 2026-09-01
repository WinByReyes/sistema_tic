package ec.gob.tic.sistema_tic.audit;

import ec.gob.tic.sistema_tic.service.AuditoriaService;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;


    public AuditoriaAspect(
            AuditoriaService auditoriaService) {

        this.auditoriaService =
                auditoriaService;
    }


    @AfterReturning(
            pointcut =
                    "@annotation(auditable)"
            ,
            returning = "resultado"
    )
    public void registrarAccion(
            JoinPoint joinPoint,
            Auditable auditable,
            Object resultado) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder
                                .getRequestAttributes();


        if (attributes == null) {
            return;
        }


        HttpServletRequest request =
                attributes.getRequest();


        String ip =
                request.getRemoteAddr();


        auditoriaService.registrar(

                auditable.modulo(),

                auditable.accion(),

                auditable.descripcion(),

                request.getMethod(),

                request.getRequestURI(),

                ip
        );
    }
}