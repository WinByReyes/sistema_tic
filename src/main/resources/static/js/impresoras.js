let modoEdicion = false;
let listaFuncionariosCache = [];

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

// ========================================
// CARGAR CATÁLOGOS CON OPCIÓN __OTRO__
// ========================================
async function cargarCatalogo(tipo, selectId, textoInicial, incluirOtro = true) {
    try {
        const respuesta = await apiFetch(`/api/catalogos/activos/${tipo}`);
        if (!respuesta || !respuesta.ok) return false;

        const datos = await respuesta.json();
        const select = document.getElementById(selectId);
        if (!select) return false;

        select.innerHTML = `<option value="">${textoInicial}</option>`;

        datos.forEach(item => {
            const opcion = document.createElement("option");
            opcion.value = item.nombre;
            opcion.textContent = item.nombre;
            select.appendChild(opcion);
        });

        if (incluirOtro) {
            const opcionOtro = document.createElement("option");
            opcionOtro.value = "__OTRO__";
            opcionOtro.textContent = "Otra opción...";
            select.appendChild(opcionOtro);

            configurarCampoOtro(selectId);
        }
        return datos.length > 0;
    } catch (error) {
        console.error(`Error cargando catálogo ${tipo}:`, error);
        return false;
    }
}

// Tipos de equipo de impresoras: usa TIPO_EQUIPO_TECNOLOGICO y conserva
// únicamente las opciones propias de impresoras.
async function cargarTiposImpresora(selectId, textoInicial, incluirOtro = true) {
    try {
        const respuesta = await apiFetch("/api/catalogos/activos/TIPO_EQUIPO_TECNOLOGICO");
        if (!respuesta || !respuesta.ok) return false;

        const datos = await respuesta.json();
        const select = document.getElementById(selectId);
        if (!select) return false;

        const tiposImpresora = datos.filter(item =>
            String(item.nombre || "").toUpperCase().includes("IMPRESORA"));

        select.innerHTML = `<option value="">${textoInicial}</option>`;

        tiposImpresora.forEach(item => {
            const opcion = document.createElement("option");
            opcion.value = item.nombre;
            opcion.textContent = item.nombre;
            select.appendChild(opcion);
        });

        if (incluirOtro) {
            const opcionOtro = document.createElement("option");
            opcionOtro.value = "__OTRO__";
            opcionOtro.textContent = "Otra opción...";
            select.appendChild(opcionOtro);

            configurarCampoOtro(selectId);
        }
        return tiposImpresora.length > 0;
    } catch (error) {
        console.error("Error cargando tipos de impresora:", error);
        return false;
    }
}

// MARCA: usa MARCA_EQUIPO_TECNOLOGICO con respaldo en MARCA (computadoras)
async function cargarMarca(includeOtro = true) {
    const tieneMarcaPropia = await cargarCatalogo("MARCA_EQUIPO_TECNOLOGICO", "marca", "Seleccione la marca", includeOtro);
    if (!tieneMarcaPropia) {
        await cargarCatalogo("MARCA", "marca", "Seleccione la marca", includeOtro);
    }
}

function configurarCampoOtro(selectId) {
    const select = document.getElementById(selectId);
    const input = document.getElementById(`${selectId}Otro`);
    if (!select || !input) return;

    select.addEventListener("change", function() {
        if (this.value === "__OTRO__") {
            input.style.display = "block";
            input.required = true;
            input.focus();
        } else {
            input.style.display = "none";
            input.required = false;
            input.value = "";
        }
    });
}

function obtenerValorCatalogo(selectId) {
    const select = document.getElementById(selectId);
    if (!select) return "";
    if (select.value === "__OTRO__") {
        const input = document.getElementById(`${selectId}Otro`);
        return input ? input.value.trim() : "";
    }
    return select.value;
}

function seleccionarValorCatalogo(selectId, valor) {
    const select = document.getElementById(selectId);
    const input = document.getElementById(`${selectId}Otro`);
    if (!select) return;

    if (!valor || valor.trim() === "") {
        select.value = "";
        if (input) {
            input.value = "";
            input.style.display = "none";
            input.required = false;
        }
        return;
    }

    const existe = Array.from(select.options).some(opt => opt.value.toLowerCase() === valor.toLowerCase());
    if (existe) {
        select.value = Array.from(select.options).find(opt => opt.value.toLowerCase() === valor.toLowerCase()).value;
        if (input) {
            input.value = "";
            input.style.display = "none";
            input.required = false;
        }
    } else {
        select.value = "__OTRO__";
        if (input) {
            input.value = valor;
            input.style.display = "block";
            input.required = true;
        }
    }
}

async function cargarTodosLosCatalogos() {
    await Promise.all([
        cargarTiposImpresora("tipoEquipo", "Seleccione el tipo de equipo"),
        cargarMarca(),
        cargarCatalogo("ESTADO_COMPUTADORA", "estado", "Seleccione el estado"),
        cargarTiposImpresora("filtroTipo", "Todos los tipos", false),
        cargarCatalogo("MARCA_EQUIPO_TECNOLOGICO", "filtroMarca", "Todas las marcas", false)
    ]);
}

// ========================================
// CARGAR FUNCIONARIOS
// ========================================
async function cargarFuncionarios() {
    try {
        const respuesta = await apiFetch("/api/funcionarios");
        if (!respuesta || !respuesta.ok) return;

        listaFuncionariosCache = await respuesta.json();
        const select = document.getElementById("funcionario");
        if (!select) return;

        select.innerHTML = `<option value="">Sin funcionario asignado</option>`;
        listaFuncionariosCache.forEach(f => {
            const opt = document.createElement("option");
            opt.value = f.cedula;
            const nombres = f.nombres || f.nombrePila || "";
            const apellidos = f.apellidos || "";
            opt.textContent = `${f.cedula} - ${nombres} ${apellidos}`.trim();
            select.appendChild(opt);
        });
    } catch (error) {
        console.error("Error al cargar funcionarios:", error);
    }
}

function alSeleccionarFuncionario() {
    const cedula = document.getElementById("funcionario")?.value;
    const inputUnidad = document.getElementById("unidadAdministrativa");
    if (!inputUnidad) return;

    if (!cedula) {
        inputUnidad.value = "";
        return;
    }

    const funcionario = listaFuncionariosCache.find(f => f.cedula === cedula);
    inputUnidad.value = funcionario ? (funcionario.unidadAdministrativa || "") : "";
}

// ========================================
// CARGAR IMPRESORAS / RENDERIZAR TABLA
// ========================================
async function cargarImpresoras(url = "/api/impresoras") {
    try {
        const respuesta = await apiFetch(url);
        if (!respuesta || !respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "Error al cargar la lista de impresoras");
            alert(mensaje);
            return;
        }

        const impresoras = await respuesta.json();
        renderizarTablaImpresoras(impresoras);
    } catch (error) {
        console.error(error);
        alert("Error al cargar la lista de impresoras");
    }
}

function renderizarTablaImpresoras(impresoras) {
    const tabla = document.getElementById("tablaImpresoras");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!impresoras || impresoras.length === 0) {
        tabla.innerHTML = `<tr><td colspan="9" class="sin-datos">No se encontraron impresoras registradas.</td></tr>`;
        return;
    }

    impresoras.forEach(imp => {
        const fila = document.createElement("tr");

        let badgeClase = "badge-info";
        const estadoNorm = (imp.estado || "").toUpperCase();
        if (estadoNorm.includes("BUENO") || estadoNorm.includes("OPERATIVO") || estadoNorm.includes("EXCELENTE")) {
            badgeClase = "badge-success";
        } else if (estadoNorm.includes("DANADO") || estadoNorm.includes("MALO") || estadoNorm.includes("BAJA")) {
            badgeClase = "badge-danger";
        } else if (estadoNorm.includes("REGULAR") || estadoNorm.includes("REVISION")) {
            badgeClase = "badge-warning";
        }

        const nombreFuncionario = imp.nombreFuncionario || "Sin asignar";
        const unidad = imp.unidadAdministrativaFuncionario ? esc(imp.unidadAdministrativaFuncionario) : "—";

        let botonEliminar = "";
        if (rol === "ADMIN") {
            botonEliminar = `
                <button type="button" class="btn-icon btn-icon-danger" title="Eliminar impresora" aria-label="Eliminar impresora" onclick="eliminarImpresora(${imp.id})">
                    🗑️
                </button>
            `;
        }

        fila.innerHTML = `
            <td><strong>${imp.id}</strong></td>
            <td>${esc(nombreFuncionario)}</td>
            <td>${unidad}</td>
            <td>${esc(imp.tipoEquipo || "—")}</td>
            <td>${esc(imp.marca || "—")}</td>
            <td>${esc(imp.modelo || "—")}</td>
            <td><strong>${esc(imp.serie || "—")}</strong></td>
            <td><span class="badge ${badgeClase}">${esc(imp.estado || "OPERATIVO")}</span></td>
            <td style="text-align: center;">
                <div class="acciones-iconos" style="justify-content: center;">
                    <button type="button" class="btn-icon btn-icon-warning" title="Editar impresora" aria-label="Editar impresora" onclick="editarImpresora(${imp.id})">
                        ✏️
                    </button>
                    ${botonEliminar}
                </div>
            </td>
        `;

        tabla.appendChild(fila);
    });
}

// ========================================
// BÚSQUEDA MULTICRITERIO
// ========================================
async function buscarImpresoras() {
    const cedula = document.getElementById("buscarCedula")?.value.trim();
    const serie = document.getElementById("buscarSerie")?.value.trim();
    const tipo = document.getElementById("filtroTipo")?.value.trim();
    const marca = document.getElementById("filtroMarca")?.value.trim();
    const modelo = document.getElementById("filtroModelo")?.value.trim();

    if (!cedula && !serie && !tipo && !marca && !modelo) {
        await cargarImpresoras();
        return;
    }

    const params = new URLSearchParams();
    if (cedula) params.append("cedula", cedula);
    if (serie) params.append("serie", serie);
    if (tipo) params.append("tipo", tipo);
    if (marca) params.append("marca", marca);
    if (modelo) params.append("modelo", modelo);

    const queryString = params.toString();
    const url = queryString ? `/api/impresoras?${queryString}` : "/api/impresoras";
    await cargarImpresoras(url);
}

function limpiarBusqueda() {
    ["buscarCedula", "buscarSerie", "filtroTipo", "filtroMarca", "filtroModelo"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = "";
    });
    cargarImpresoras();
}

// ========================================
// ABRIR / CERRAR FORMULARIO
// ========================================
async function abrirFormulario() {
    modoEdicion = false;
    const form = document.getElementById("impresoraForm");
    if (form) form.reset();

    document.getElementById("impresoraId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nueva impresora";
    document.getElementById("unidadAdministrativa").value = "";

    await cargarFuncionarios();
    await cargarTodosLosCatalogos();

    const panel = document.getElementById("formularioImpresora");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });

    setTimeout(() => {
        document.getElementById("tipoEquipo")?.focus();
    }, 250);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioImpresora");
    panel.style.display = "none";
    document.getElementById("impresoraForm")?.reset();
}

// ========================================
// GUARDAR / ACTUALIZAR
// ========================================
document.getElementById("impresoraForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("impresoraId").value;
    const cedulaFuncionario = document.getElementById("funcionario").value || null;

    const datos = {
        cedulaFuncionario,
        tipoEquipo: obtenerValorCatalogo("tipoEquipo"),
        marca: obtenerValorCatalogo("marca"),
        modelo: document.getElementById("modelo").value.trim(),
        serie: document.getElementById("serie").value.trim(),
        estado: obtenerValorCatalogo("estado")
    };

    if (!datos.tipoEquipo || !datos.marca || !datos.estado) {
        alert("Por favor complete todos los campos obligatorios marcados con asterisco (*).");
        return;
    }

    try {
        let respuesta;
        if (!modoEdicion) {
            respuesta = await apiFetch("/api/impresoras", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/impresoras/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar la impresora");
            alert(mensaje);
            return;
        }

        alert(modoEdicion ? "Impresora actualizada correctamente" : "Impresora creada correctamente");
        cerrarFormulario();
        await cargarImpresoras();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ========================================
// EDITAR IMPRESORA
// ========================================
async function editarImpresora(id) {
    try {
        const respuesta = await apiFetch(`/api/impresoras/${id}`);
        if (!respuesta || !respuesta.ok) throw new Error("Impresora no encontrada");

        const imp = await respuesta.json();
        modoEdicion = true;

        document.getElementById("impresoraId").value = imp.id;
        document.getElementById("tituloFormulario").textContent = "Editar impresora";

        await cargarFuncionarios();
        await cargarTodosLosCatalogos();

        const cedula = imp.cedulaFuncionario || (imp.funcionario ? imp.funcionario.cedula : "");
        document.getElementById("funcionario").value = cedula;
        alSeleccionarFuncionario();

        document.getElementById("modelo").value = imp.modelo || "";
        document.getElementById("serie").value = imp.serie || "";

        seleccionarValorCatalogo("tipoEquipo", imp.tipoEquipo);
        seleccionarValorCatalogo("marca", imp.marca);
        seleccionarValorCatalogo("estado", imp.estado);

        const panel = document.getElementById("formularioImpresora");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });

        setTimeout(() => {
            document.getElementById("tipoEquipo")?.focus();
        }, 250);
    } catch (error) {
        console.error(error);
        alert("Error al cargar los datos de la impresora");
    }
}

// ========================================
// ELIMINAR IMPRESORA
// ========================================
async function eliminarImpresora(id) {
    if (!confirm("¿Está seguro de eliminar esta impresora permanentemente?")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/impresoras/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar la impresora");
            alert(mensaje);
            return;
        }

        alert("Impresora eliminada correctamente");
        await cargarImpresoras();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
}

// ========================================
// UTILIDADES
// ========================================
function esc(valor) {
    if (valor === null || valor === undefined || valor === "") return "N/A";
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
async function iniciarPagina() {
    await cargarFuncionarios();
    await cargarTodosLosCatalogos();
    await cargarImpresoras();

    const idEdicion = new URLSearchParams(window.location.search).get("editar");
    if (/^\d+$/.test(idEdicion || "")) {
        window.history.replaceState({}, document.title, window.location.pathname);
        await editarImpresora(Number(idEdicion));
    }
}

iniciarPagina();