// ========================================
// SESIÓN Y PERMISOS
// ========================================

const token = localStorage.getItem("token");
const rol = localStorage.getItem("rol");

if (!token) {
    window.location.href = "/login";
}

const menuAdministracion = document.getElementById("menuAdministracion");
if (menuAdministracion && rol !== "ADMIN") {
    document.querySelectorAll(".menu-dropdown .submenu .submenu-item").forEach(item => {
        const href = item.getAttribute("href") || "";
        if (["/usuarios", "/auditoria", "/respaldos", "/configuracion-institucional"].some(p => href.includes(p))) {
            item.style.display = "none";
        }
    });
}

// ========================================
// CARGAR CATÁLOGOS
// ========================================

async function cargarCatalogos() {
    const tipo = document.getElementById("tipoCatalogo").value;
    const tabla = document.getElementById("tablaCatalogos");

    tabla.innerHTML = `<tr><td colspan="4" class="sin-datos">Cargando elementos...</td></tr>`;

    try {
        const respuesta = await apiFetch(`/api/catalogos/${tipo}`);

        if (!respuesta) return;

        if (!respuesta.ok) {
            throw new Error("No se pudieron cargar los catálogos");
        }

        const datos = await respuesta.json();
        tabla.innerHTML = "";

        if (!datos || datos.length === 0) {
            tabla.innerHTML = `<tr><td colspan="4" class="sin-datos">No hay elementos registrados en este catálogo.</td></tr>`;
            return;
        }

        datos.forEach(catalogo => {
            const fila = document.createElement("tr");

            const estadoBadge = catalogo.activo
                ? `<span class="badge badge-success">Activo</span>`
                : `<span class="badge badge-danger">Inactivo</span>`;

            fila.innerHTML = `
                <td><strong>${catalogo.id}</strong></td>
                <td>${escapeHtml(catalogo.nombre)}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-iconos" style="justify-content: center;">
                        <button
                            type="button"
                            class="btn-icon btn-icon-warning"
                            title="Editar elemento"
                            aria-label="Editar elemento"
                            onclick="editarCatalogo(${catalogo.id}, '${escapeHtml(catalogo.nombre)}', ${catalogo.activo})">
                            ✏️
                        </button>
                        ${
                            catalogo.activo
                                ? `<button
                                    type="button"
                                    class="btn-icon btn-icon-danger"
                                    title="Desactivar elemento"
                                    aria-label="Desactivar elemento"
                                    onclick="desactivarCatalogo(${catalogo.id})">
                                    ⛔
                                   </button>`
                                : `<button
                                    type="button"
                                    class="btn-icon btn-icon-success"
                                    title="Activar elemento"
                                    aria-label="Activar elemento"
                                    onclick="activarCatalogo(${catalogo.id})">
                                    ✓
                                   </button>`
                        }
                    </div>
                </td>
            `;

            tabla.appendChild(fila);
        });

    } catch (error) {
        console.error(error);
        tabla.innerHTML = `<tr><td colspan="4" class="sin-datos text-danger">Error al cargar los catálogos.</td></tr>`;
    }
}

// ========================================
// ESCAPAR HTML
// ========================================

function escapeHtml(text) {
    if (text === null || text === undefined) return "";
    return String(text)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// ========================================
// MODAL
// ========================================

function abrirModalCatalogo() {
    document.getElementById("catalogoId").value = "";
    document.getElementById("nombreCatalogo").value = "";
    document.getElementById("tituloModal").textContent = "Nuevo Elemento de Catálogo";
    document.getElementById("modalCatalogo").style.display = "flex";
    setTimeout(() => document.getElementById("nombreCatalogo").focus(), 50);
}

function cerrarModalCatalogo() {
    document.getElementById("modalCatalogo").style.display = "none";
}

// ========================================
// GUARDAR
// ========================================

async function guardarCatalogo() {
    const id = document.getElementById("catalogoId").value;
    const nombre = document.getElementById("nombreCatalogo").value.trim();
    const tipo = document.getElementById("tipoCatalogo").value;

    if (!nombre) {
        alert("Debe ingresar un nombre o valor.");
        document.getElementById("nombreCatalogo").focus();
        return;
    }

    const datos = {
        tipo: tipo,
        nombre: nombre,
        activo: true
    };

    try {
        let respuesta;

        if (id) {
            respuesta = await apiFetch(`/api/catalogos/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch("/api/catalogos", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el catálogo");
            alert(mensaje);
            return;
        }

        cerrarModalCatalogo();
        await cargarCatalogos();

    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor.");
    }
}

// ========================================
// EDITAR
// ========================================

function editarCatalogo(id, nombre, activo) {
    document.getElementById("catalogoId").value = id;
    document.getElementById("nombreCatalogo").value = nombre;
    document.getElementById("tituloModal").textContent = "Editar Elemento de Catálogo";
    document.getElementById("modalCatalogo").style.display = "flex";
    setTimeout(() => document.getElementById("nombreCatalogo").focus(), 50);
}

// ========================================
// DESACTIVAR / ACTIVAR
// ========================================

async function desactivarCatalogo(id) {
    if (!confirm("¿Desea desactivar este elemento del catálogo?")) {
        return;
    }

    const respuesta = await apiFetch(`/api/catalogos/${id}/desactivar`, {
        method: "PATCH"
    });

    if (respuesta && respuesta.ok) {
        await cargarCatalogos();
    }
}

async function activarCatalogo(id) {
    const respuesta = await apiFetch(`/api/catalogos/${id}/activar`, {
        method: "PATCH"
    });

    if (respuesta && respuesta.ok) {
        await cargarCatalogos();
    }
}

// ========================================
// CAMBIO DE CATÁLOGO
// ========================================

const selectorCatalogo = document.getElementById("tipoCatalogo");
if (selectorCatalogo) {
    selectorCatalogo.addEventListener("change", cargarCatalogos);
}

// ========================================
// INICIO
// ========================================

cargarCatalogos();
