let modoEdicion = false;
let listaUsuariosGlobal = [];

// ================================
// JWT Y SESIÓN
// ================================

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
    localStorage.removeItem("idUsuario");
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

// ================================
// CARGAR USUARIOS
// ================================

async function cargarUsuarios() {
    const tabla = document.getElementById("tablaUsuarios");
    tabla.innerHTML = `<tr><td colspan="6" class="sin-datos">Cargando usuarios...</td></tr>`;

    try {
        const respuesta = await apiFetch("/api/usuarios");

        if (!respuesta) return;

        if (!respuesta.ok) {
            throw new Error("No se pudieron obtener los usuarios");
        }

        listaUsuariosGlobal = await respuesta.json();
        renderizarTablaUsuarios(listaUsuariosGlobal);

    } catch (error) {
        console.error(error);
        tabla.innerHTML = `<tr><td colspan="6" class="sin-datos text-danger">Error al cargar los usuarios.</td></tr>`;
    }
}

function renderizarTablaUsuarios(usuarios) {
    const tabla = document.getElementById("tablaUsuarios");
    tabla.innerHTML = "";

    if (!usuarios || usuarios.length === 0) {
        tabla.innerHTML = `<tr><td colspan="6" class="sin-datos">No se encontraron usuarios registrados.</td></tr>`;
        return;
    }

    usuarios.forEach(usuario => {
        const fila = document.createElement("tr");

        const estadoBadge = usuario.estado
            ? `<span class="badge badge-success">Activo</span>`
            : `<span class="badge badge-danger">Inactivo</span>`;

        const rolBadge = usuario.rol === "ADMIN"
            ? `<span class="badge badge-warning">ADMIN</span>`
            : `<span class="badge badge-info">TÉCNICO</span>`;

        fila.innerHTML = `
            <td><strong>${usuario.id}</strong></td>
            <td><strong>${escapeHtml(usuario.nombre)}</strong></td>
            <td>${escapeHtml(usuario.usuario)}</td>
            <td>${rolBadge}</td>
            <td>${estadoBadge}</td>
            <td>
                <div class="acciones-iconos" style="justify-content: center;">
                    <button
                        type="button"
                        class="btn-icon btn-icon-warning"
                        title="Editar usuario"
                        aria-label="Editar usuario"
                        onclick="editarUsuario(${usuario.id})">
                        ✏️
                    </button>
                    <button
                        type="button"
                        class="btn-icon btn-icon-danger"
                        title="Eliminar usuario"
                        aria-label="Eliminar usuario"
                        onclick="eliminarUsuario(${usuario.id})">
                        🗑️
                    </button>
                </div>
            </td>
        `;

        tabla.appendChild(fila);
    });
}

// ================================
// FILTRAR EN CLIENTE
// ================================

const buscarInput = document.getElementById("buscarUsuario");
if (buscarInput) {
    buscarInput.addEventListener("input", () => {
        const query = buscarInput.value.toLowerCase().trim();
        if (!query) {
            renderizarTablaUsuarios(listaUsuariosGlobal);
            return;
        }
        const filtrados = listaUsuariosGlobal.filter(u =>
            (u.nombre && u.nombre.toLowerCase().includes(query)) ||
            (u.usuario && u.usuario.toLowerCase().includes(query)) ||
            (u.rol && u.rol.toLowerCase().includes(query))
        );
        renderizarTablaUsuarios(filtrados);
    });
}

// ================================
// ABRIR / CERRAR FORMULARIO
// ================================

function abrirFormulario() {
    modoEdicion = false;
    document.getElementById("usuarioForm").reset();
    document.getElementById("usuarioId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nuevo Usuario";
    document.getElementById("contrasena").required = true;

    const panel = document.getElementById("formularioUsuario");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });
    setTimeout(() => document.getElementById("nombre").focus(), 100);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioUsuario");
    panel.style.display = "none";
    document.getElementById("usuarioForm").reset();
}

// ================================
// GUARDAR USUARIO
// ================================

document.getElementById("usuarioForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("usuarioId").value;
    const nombre = document.getElementById("nombre").value.trim();
    const usuario = document.getElementById("usuario").value.trim();
    const contrasena = document.getElementById("contrasena").value;
    const rolSeleccionado = document.getElementById("rol").value;
    const estado = document.getElementById("estado").value === "true";

    const datos = {
        nombre: nombre,
        usuario: usuario,
        rol: rolSeleccionado,
        estado: estado
    };

    if (contrasena) {
        datos.contrasena = contrasena;
    }

    try {
        let respuesta;

        if (!modoEdicion) {
            respuesta = await apiFetch("/api/usuarios", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/usuarios/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const errorMsg = await obtenerMensajeError(respuesta, "No se pudo guardar el usuario");
            alert(errorMsg);
            return;
        }

        cerrarFormulario();
        await cargarUsuarios();

    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ================================
// EDITAR USUARIO
// ================================

async function editarUsuario(id) {
    try {
        const respuesta = await apiFetch(`/api/usuarios/${id}`);

        if (!respuesta) return;

        if (!respuesta.ok) {
            throw new Error("No se encontró el usuario");
        }

        const usuario = await respuesta.json();
        modoEdicion = true;

        document.getElementById("usuarioId").value = usuario.id;
        document.getElementById("nombre").value = usuario.nombre || "";
        document.getElementById("usuario").value = usuario.usuario || "";
        document.getElementById("contrasena").value = "";
        document.getElementById("contrasena").required = false;
        document.getElementById("rol").value = usuario.rol || "TECNICO";
        document.getElementById("estado").value = usuario.estado ? "true" : "false";

        document.getElementById("tituloFormulario").textContent = `Editar Usuario: ${usuario.usuario}`;

        const panel = document.getElementById("formularioUsuario");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });
        setTimeout(() => document.getElementById("nombre").focus(), 100);

    } catch (error) {
        console.error(error);
        alert("Error al obtener el usuario");
    }
}

// ================================
// ELIMINAR USUARIO
// ================================

async function eliminarUsuario(id) {
    if (!confirm("¿Está seguro de eliminar este usuario?")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/usuarios/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const errorMsg = await obtenerMensajeError(respuesta, "No se pudo eliminar el usuario");
            alert(errorMsg);
            return;
        }

        await cargarUsuarios();

    } catch (error) {
        console.error(error);
        alert("Error al eliminar el usuario");
    }
}

function escapeHtml(text) {
    if (text === null || text === undefined) return "";
    return String(text)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// ================================
// INICIO
// ================================

cargarUsuarios();
