// ========================================
// VALIDAR SESIÓN
// ========================================

const token =
    localStorage.getItem("token");

const rol =
    localStorage.getItem("rol");

if (!token) {

    window.location.href =
        "/login";

}


// ========================================
// SOLO ADMIN
// ========================================

if (rol !== "ADMIN") {

    alert(
        "No tiene permisos para acceder a Administración."
    );

    window.location.href =
        "/dashboard";
}


// ========================================
// DATOS DEL USUARIO
// ========================================

const nombre =
    localStorage.getItem("nombre");

const usuario =
    localStorage.getItem("usuario");


const nombreUsuario =
    document.getElementById(
        "nombreUsuario"
    );

const rolUsuario =
    document.getElementById(
        "rolUsuario"
    );

const avatarUsuario =
    document.getElementById(
        "avatarUsuario"
    );


if (nombreUsuario) {

    nombreUsuario.textContent =
        nombre || usuario || "Administrador";
}


if (rolUsuario) {

    rolUsuario.textContent =
        rol || "ADMIN";
}


if (avatarUsuario) {

    avatarUsuario.textContent =
        (
            nombre ||
            usuario ||
            "A"
        )
            .charAt(0)
            .toUpperCase();
}


// ========================================
// LOGOUT
// ========================================

document
    .getElementById("logout")
    ?.addEventListener(
        "click",
        function(event) {

            event.preventDefault();

            localStorage.clear();

            window.location.href =
                "/login";

        }
    );


// ========================================
// RESPONSABLES / FUNCIONARIOS
// ========================================

document.getElementById("responsablesCard")?.addEventListener("click", function() {
    window.location.href = "/funcionarios";
});

// ========================================
// AUDITORÍA
// ========================================

const modalAuditoria = document.getElementById("modalAuditoria");

document.getElementById("auditoriaCard")?.addEventListener("click", async function () {

    const respuesta = await apiFetch("/api/auditoria");
    if (!respuesta || !respuesta.ok) return;

    const registros = await respuesta.json();
    const tbody = document.getElementById("auditoriaTablaBody");

    tbody.innerHTML = registros.map(r => `
        <tr>
            <td>${new Date(r.fechaHora).toLocaleString("es-EC")}</td>
            <td>${r.usuario}</td>
            <td>${r.rol}</td>
            <td>${r.modulo}</td>
            <td>${r.accion}</td>
            <td>${r.descripcion}</td>
        </tr>
    `).join("");

    modalAuditoria.style.display = "flex";
});

document.getElementById("cerrarModalAuditoria")?.addEventListener("click", () => {
    modalAuditoria.style.display = "none";
});


// ========================================
// RESPALDOS
// ========================================

const modalRespaldos = document.getElementById("modalRespaldos");

document.getElementById("respaldosCard")?.addEventListener("click", () => {
    document.getElementById("respaldoResultado").textContent = "";
    modalRespaldos.style.display = "flex";
});

document.getElementById("cerrarModalRespaldos")?.addEventListener("click", () => {
    modalRespaldos.style.display = "none";
});

document.getElementById("generarRespaldoBtn")?.addEventListener("click", async function () {

    const resultado = document.getElementById("respaldoResultado");
    resultado.textContent = "Generando respaldo...";

    const respuesta = await apiFetch("/api/backups", { method: "POST" });
    if (!respuesta) return;

    const datos = await respuesta.json();

    resultado.textContent = respuesta.ok
        ? `✔ ${datos.mensaje || "Respaldo generado"}: ${datos.archivo || ""}`
        : `✖ ${datos.mensaje || "Error al generar respaldo"}`;
});


// ========================================
// CONFIGURACIÓN INSTITUCIONAL
// ========================================

const modalConfigInstitucional = document.getElementById("modalConfigInstitucional");

document.getElementById("configInstitucionalCard")?.addEventListener("click", async function () {

    const respuesta = await apiFetch("/api/configuracion-institucional");
    if (!respuesta || !respuesta.ok) return;

    const config = await respuesta.json();

    document.getElementById("ci_nombreInstitucion").value = config.nombreInstitucion || "";
    document.getElementById("ci_unidadAdministrativa").value = config.unidadAdministrativa || "";
    document.getElementById("ci_direccion").value = config.direccion || "";
    document.getElementById("ci_telefono").value = config.telefono || "";
    document.getElementById("ci_correo").value = config.correo || "";

    modalConfigInstitucional.style.display = "flex";
});

document.getElementById("cerrarModalConfigInstitucional")?.addEventListener("click", () => {
    modalConfigInstitucional.style.display = "none";
});

document.getElementById("formConfigInstitucional")?.addEventListener("submit", async function (event) {

    event.preventDefault();

    const datos = {
        nombreInstitucion: document.getElementById("ci_nombreInstitucion").value,
        unidadAdministrativa: document.getElementById("ci_unidadAdministrativa").value,
        direccion: document.getElementById("ci_direccion").value,
        telefono: document.getElementById("ci_telefono").value,
        correo: document.getElementById("ci_correo").value
    };

    const respuesta = await apiFetch("/api/configuracion-institucional", {
        method: "PUT",
        body: JSON.stringify(datos)
    });

    if (!respuesta) return;

    if (respuesta.ok) {
        alert("Configuración actualizada correctamente.");
        modalConfigInstitucional.style.display = "none";
    } else {
        const errorMsg = await obtenerMensajeError(respuesta, "No se pudo actualizar la configuración.");
        alert(errorMsg);
    }
});

// ========================================
// CATÁLOGOS
// ========================================

document.getElementById("catalogosCard")?.addEventListener("click", function () {
    window.location.href = "/catalogos";
});
