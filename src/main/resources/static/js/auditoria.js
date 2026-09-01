// ========================================
// SESIÓN
// ========================================

const token =
    localStorage.getItem(
        "token"
    );


const rol =
    localStorage.getItem(
        "rol"
    );


if (!token) {

    window.location.href =
        "/login";

}


if (rol !== "ADMIN") {

    alert(
        "No tiene permisos para ver la auditoría."
    );

    window.location.href =
        "/dashboard";

}


// ========================================
// ESTADO
// ========================================

let registrosAuditoria = [];


// ========================================
// CARGAR AUDITORÍA
// ========================================

async function cargarAuditoria() {

    const respuesta =
        await apiFetch(
            "/api/auditoria"
        );

    if (!respuesta) {
        return;
    }

    if (!respuesta.ok) {

        alert(
            "No se pudo cargar la auditoría."
        );

        return;
    }


    registrosAuditoria =
        await respuesta.json();

    renderizarAuditoria();

}


// ========================================
// RENDERIZAR TABLA
// ========================================

function renderizarAuditoria() {

    const usuarioFiltro =
        document
            .getElementById("filtroUsuario")
            .value
            .trim()
            .toLowerCase();

    const moduloFiltro =
        document
            .getElementById("filtroModulo")
            .value
            .trim()
            .toLowerCase();

    const accionFiltro =
        document
            .getElementById("filtroAccion")
            .value
            .trim()
            .toLowerCase();


    const filtrados =
        registrosAuditoria.filter(
            registro =>

                (registro.usuario || "")
                    .toLowerCase()
                    .includes(usuarioFiltro) &&

                (registro.modulo || "")
                    .toLowerCase()
                    .includes(moduloFiltro) &&

                (registro.accion || "")
                    .toLowerCase()
                    .includes(accionFiltro)
        );


    const tbody =
        document.getElementById(
            "tablaAuditoria"
        );


    if (filtrados.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="8">
                    No se encontraron registros con los filtros aplicados.
                </td>
            </tr>
        `;

        return;
    }


    tbody.innerHTML =
        filtrados
            .map(registro => `
                <tr>
                    <td>${formatearFecha(registro.fechaHora)}</td>
                    <td>${registro.usuario ?? ""}</td>
                    <td>${registro.rol ?? ""}</td>
                    <td>${registro.modulo ?? ""}</td>
                    <td>${registro.accion ?? ""}</td>
                    <td>${registro.descripcion ?? ""}</td>
                    <td>${registro.metodoHttp ?? ""}</td>
                    <td>${registro.ip ?? ""}</td>
                </tr>
            `)
            .join("");

}


// ========================================
// UTILIDADES
// ========================================

function formatearFecha(fechaHora) {

    if (!fechaHora) {
        return "";
    }

    return new Date(fechaHora)
        .toLocaleString("es-EC");

}


// ========================================
// FILTROS
// ========================================

document
    .getElementById("filtroUsuario")
    ?.addEventListener(
        "input",
        renderizarAuditoria
    );

document
    .getElementById("filtroModulo")
    ?.addEventListener(
        "input",
        renderizarAuditoria
    );

document
    .getElementById("filtroAccion")
    ?.addEventListener(
        "input",
        renderizarAuditoria
    );


// ========================================
// INICIO
// ========================================

cargarAuditoria();
