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
        "No tiene permisos para generar respaldos."
    );

    window.location.href =
        "/dashboard";

}


// ========================================
// GENERAR RESPALDO
// ========================================

const generarRespaldoBtn =
    document.getElementById(
        "generarRespaldoBtn"
    );

const panelResultado =
    document.getElementById(
        "panelResultado"
    );

const resultadoRespaldo =
    document.getElementById(
        "resultadoRespaldo"
    );



const tablaRespaldos =
    document.getElementById("tablaRespaldos");


// ========================================
// CARGAR HISTORIAL
// ========================================

async function cargarHistorialRespaldos() {

    const respuesta = await apiFetch("/api/backups");
    if (!respuesta) return;

    if (!respuesta.ok) {
        tablaRespaldos.innerHTML = `
            <tr><td colspan="3">No se pudo cargar el historial de respaldos.</td></tr>
        `;
        return;
    }

    const respaldos = await respuesta.json();
    renderizarHistorial(respaldos);
}


function renderizarHistorial(respaldos) {

    if (!respaldos || respaldos.length === 0) {
        tablaRespaldos.innerHTML = `
            <tr><td colspan="3">Aún no se han generado respaldos.</td></tr>
        `;
        return;
    }

    tablaRespaldos.innerHTML = respaldos.map(respaldo => `
        <tr>
            <td>${respaldo.fechaCreacion ? new Date(respaldo.fechaCreacion).toLocaleString("es-EC") : "—"}</td>
            <td>${respaldo.nombreArchivo}</td>
            <td>${formatearTamanio(respaldo.tamanioBytes)}</td>
        </tr>
    `).join("");
}


function formatearTamanio(bytes) {
    if (!bytes || bytes <= 0) return "—";
    const kb = bytes / 1024;
    if (kb < 1024) return `${kb.toFixed(1)} KB`;
    return `${(kb / 1024).toFixed(1)} MB`;
}


// ========================================
// GENERAR RESPALDO
// ========================================

generarRespaldoBtn?.addEventListener("click", async function () {

    generarRespaldoBtn.disabled = true;
    generarRespaldoBtn.textContent = "Generando respaldo...";

    const respuesta = await apiFetch("/api/backups", { method: "POST" });

    generarRespaldoBtn.disabled = false;
    generarRespaldoBtn.textContent = "Generar respaldo ahora";

    if (!respuesta) return;

    const datos = await respuesta.json();
    if (panelResultado) panelResultado.style.display = "block";

    if (respuesta.ok) {

        if (resultadoRespaldo) {
            resultadoRespaldo.innerHTML = `
                <p style="color: var(--color-success, #16a34a);">✔ ${datos.mensaje}</p>
                <p><strong>Archivo:</strong> ${datos.archivo}</p>
            `;
        }

        await cargarHistorialRespaldos();

    } else {

        if (resultadoRespaldo) {
            resultadoRespaldo.innerHTML = `
                <p style="color: var(--color-danger, #dc2626);">✖ ${datos.mensaje}</p>
                ${datos.error ? `<p><strong>Detalle:</strong> ${datos.error}</p>` : ""}
            `;
        }
    }
});


// ========================================
// INICIO
// ========================================

cargarHistorialRespaldos();
