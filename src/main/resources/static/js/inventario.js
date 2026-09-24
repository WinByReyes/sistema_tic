// ========================================
// JWT Y SESIÓN
// ========================================
const token = localStorage.getItem("token");
const rol = localStorage.getItem("rol");

if (!token) {
    window.location.href = "/login";
}

function cerrarSesion() {
    localStorage.removeItem("token");
    localStorage.removeItem("rol");
    localStorage.removeItem("usuario");
    localStorage.removeItem("nombre");
    window.location.href = "/login";
}

const botonLogout = document.getElementById("logout");
if (botonLogout) {
    botonLogout.addEventListener("click", function(event) {
        event.preventDefault();
        cerrarSesion();
    });
}

const menuAdministracion = document.getElementById("menuAdministracion");
if (menuAdministracion && rol !== "ADMIN") {
    menuAdministracion.href = "/catalogos";
    document.querySelectorAll('a.menu-item[href="/usuarios"]').forEach(item => {
        item.style.display = "none";
    });
    document.querySelectorAll(".menu-dropdown .submenu .submenu-item").forEach(item => {
        const href = item.getAttribute("href") || "";
        if (["/usuarios", "/auditoria", "/respaldos", "/configuracion-institucional"].some(p => href.includes(p))) {
            item.style.display = "none";
        }
    });
}

let itemsInventario = [];

// ========================================
// CARGAR INVENTARIO GENERAL
// ========================================
async function cargarInventario() {
    try {
        const respuesta = await apiFetch("/api/inventario/general");
        if (!respuesta || !respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "Error al cargar el inventario");
            alert(mensaje);
            return;
        }

        itemsInventario = await respuesta.json();
        renderizarInventario(itemsInventario);
    } catch (error) {
        console.error(error);
        alert("Error al cargar el inventario");
    }
}

// ========================================
// RENDERIZAR INVENTARIO
// ========================================
function renderizarInventario(items) {
    const datos = items || [];

    const totalComputadoras = contarPorTipo(datos, "Computadora");
    const totalPerifericos = contarPorTipo(datos, "Periférico");
    const totalImpresoras = contarPorTipo(datos, "Impresora");

    document.getElementById("totalComputadoras").textContent = totalComputadoras;
    document.getElementById("totalPerifericos").textContent = totalPerifericos;
    document.getElementById("totalImpresoras").textContent = totalImpresoras;
    document.getElementById("totalGeneral").textContent = datos.length;

    renderizarTabla(datos);
}

function contarPorTipo(items, tipo) {
    return items.filter(item => item.tipo === tipo).length;
}

function renderizarTabla(items) {
    const tabla = document.getElementById("tablaInventarioGeneral");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!items || items.length === 0) {
        tabla.innerHTML = `<tr><td colspan="8" class="sin-datos">No hay registros en el inventario.</td></tr>`;
        return;
    }

    const fragmento = document.createDocumentFragment();
    items.forEach(item => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td><span class="badge badge-info">${esc(item.tipo)}</span></td>
            <td>${esc(item.funcionario)}</td>
            <td>${esc(item.unidadAdministrativa)}</td>
            <td>${esc(item.tipoEquipo)}</td>
            <td>${esc(item.marca)}</td>
            <td>${esc(item.modelo)}</td>
            <td>${esc(item.serie)}</td>
            <td><span class="badge ${claseBadgeEstado(item.estado)}">${esc(item.estado)}</span></td>
        `;
        fragmento.appendChild(fila);
    });
    tabla.appendChild(fragmento);
}

function claseBadgeEstado(estado) {
    let badgeClase = "badge-info";
    const estadoNorm = String(estado || "").toUpperCase();
    if (estadoNorm.includes("BUENO") || estadoNorm.includes("OPERATIVO") || estadoNorm.includes("EXCELENTE")) {
        badgeClase = "badge-success";
    } else if (estadoNorm.includes("DANADO") || estadoNorm.includes("MALO") || estadoNorm.includes("BAJA")) {
        badgeClase = "badge-danger";
    } else if (estadoNorm.includes("REGULAR") || estadoNorm.includes("REVISION")) {
        badgeClase = "badge-warning";
    }
    return badgeClase;
}

// ========================================
// IMPRIMIR INVENTARIO
// ========================================
function imprimirInventario() {
    if (!itemsInventario || itemsInventario.length === 0) {
        alert("El inventario aún no se ha cargado. Intente nuevamente.");
        return;
    }

    const ventana = window.open("", "_blank");
    if (!ventana) {
        alert("El navegador bloqueó la ventana del reporte. Permita las ventanas emergentes e intente nuevamente.");
        return;
    }

    const filas = filasInventario(itemsInventario);
    const totalComputadoras = contarPorTipo(itemsInventario, "Computadora");
    const totalPerifericos = contarPorTipo(itemsInventario, "Periférico");
    const totalImpresoras = contarPorTipo(itemsInventario, "Impresora");
    const totalGeneral = itemsInventario.length;

    ventana.document.write(`
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>Inventario General - Sistema TIC</title>
            <link rel="stylesheet" href="/css/estilos.css">
            <style>
                body { padding: 30px; background: #fff; font-family: Arial, sans-serif; }
                .reporte-encabezado { margin-bottom: 20px; border-bottom: 2px solid #2563eb; padding-bottom: 10px; }
                .reporte-encabezado h2 { margin: 0 0 4px; }
                .reporte-encabezado h3 { margin: 0 0 4px; }
                .reporte-encabezado p { margin: 0; color: #475569; font-size: 12px; }
                .totales { display: flex; gap: 16px; margin-bottom: 20px; }
                .total-box { flex: 1; border: 1px solid #cbd5e1; border-radius: 8px; padding: 12px; text-align: center; }
                .total-box span { display: block; font-size: 11px; color: #64748b; }
                .total-box strong { font-size: 22px; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                th, td { border: 1px solid #cbd5e1; padding: 6px 10px; font-size: 12px; }
                th { background: #f1f5f9; }
                @media print { body { padding: 0; } }
            </style>
        </head>
        <body>
            <header class="reporte-encabezado">
                <h2>SISTEMA TIC - GOBIERNO DEL ECUADOR</h2>
                <h3>INVENTARIO GENERAL CONSOLIDADO</h3>
                <p>Consolidado de computadoras, periféricos e impresoras | Fecha de emisión: ${esc(new Date().toLocaleString("es-EC"))}</p>
            </header>

            <div class="totales">
                <div class="total-box">
                    <span>Computadoras</span>
                    <strong>${totalComputadoras}</strong>
                </div>
                <div class="total-box">
                    <span>Periféricos</span>
                    <strong>${totalPerifericos}</strong>
                </div>
                <div class="total-box">
                    <span>Impresoras</span>
                    <strong>${totalImpresoras}</strong>
                </div>
                <div class="total-box">
                    <span>Total general</span>
                    <strong>${totalGeneral}</strong>
                </div>
            </div>

            <table>
                <thead>
                <tr>
                    <th>Tipo</th>
                    <th>Funcionario</th>
                    <th>Unidad administrativa</th>
                    <th>Tipo equipo</th>
                    <th>Marca</th>
                    <th>Modelo</th>
                    <th>Serie</th>
                    <th>Estado</th>
                </tr>
                </thead>
                <tbody>${filas}</tbody>
            </table>

            <script>window.addEventListener("load", () => window.print());<\/script>
        </body>
        </html>
    `);
    ventana.document.close();
}

function filasInventario(items) {
    if (!items || items.length === 0) {
        return `<tr><td colspan="8">Sin registros</td></tr>`;
    }
    return items.map(item => `
        <tr>
            <td>${esc(item.tipo)}</td>
            <td>${esc(item.funcionario)}</td>
            <td>${esc(item.unidadAdministrativa)}</td>
            <td>${esc(item.tipoEquipo)}</td>
            <td>${esc(item.marca)}</td>
            <td>${esc(item.modelo)}</td>
            <td>${esc(item.serie)}</td>
            <td>${esc(item.estado)}</td>
        </tr>
    `).join("");
}

// ========================================
// UTILIDADES
// ========================================
function esc(valor) {
    if (valor === null || valor === undefined) return "N/A";
    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// ========================================
// INICIO
// ========================================
cargarInventario();
