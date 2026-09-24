package ec.gob.tic.sistema_tic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    @GetMapping("/")
    public String Default()
    {
        return "login";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios()
    {
        return "usuarios/usuarios";
    }

    @GetMapping("/funcionarios")
    public String funcionarios()
    {
        return "funcionarios/funcionarios";
    }

    @GetMapping("/computadoras")
    public String computadoras()
    {
        return "computadoras/computadoras";
    }

    @GetMapping("/equipos-tecnologicos")
    public String equiposTecnologicos()
    {
        return "equipos-tecnologicos/equipos-tecnologicos";
    }

    @GetMapping("/impresoras")
    public String impresoras()
    {
        return "impresoras/impresoras";
    }

    @GetMapping("/inventario")
    public String inventario()
    {
        return "inventario/inventario";
    }

    @GetMapping("/mantenimientos")
    public String mantenimientos()
    {
        return "mantenimientos/mantenimientos";
    }

    @GetMapping("/consultas")
    public String consultas()
    {
        return "consultas/consultas";
    }

    @GetMapping("/administracion")
    public String administracion() {

        return "administracion/administracion";
    }

    @GetMapping("/catalogos")
    public String catalogos() {

        return "catalogos/catalogos";
    }

    @GetMapping("/auditoria")
    public String auditoria() {

        return "auditoria/auditoria";
    }

    @GetMapping("/respaldos")
    public String respaldos() {

        return "respaldos/respaldos";
    }

    @GetMapping("/configuracion-institucional")
    public String configuracionInstitucional() {

        return "configuracion/configuracion";
    }
}