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
async function cargarCatalogo(tipo, selectId, textoInicial) {
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

        // Opción personalizada "Otro"
        const opcionOtro = document.createElement("option");
        opcionOtro.value = "__OTRO__";
        opcionOtro.textContent = "Otra opción...";
        select.appendChild(opcionOtro);

        configurarCampoOtro(selectId);
        return datos.length > 0;
    } catch (error) {
        console.error(`Error cargando catálogo ${tipo}:`, error);
        return false;
    }
}

// MARCA: usa MARCA_EQUIPO_TECNOLOGICO con respaldo en MARCA (computadoras)
async function cargarMarca() {
    const tieneMarcaPropia = await cargarCatalogo("MARCA_EQUIPO_TECNOLOGICO", "marca", "Seleccione la marca");
    if (!tieneMarcaPropia) {
        await cargarCatalogo("MARCA", "marca", "Seleccione la marca");
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
        cargarCatalogo("TIPO_EQUIPO_TECNOLOGICO", "tipoEquipo", "Seleccione el tipo de equipo"),
        cargarMarca(),
        cargarCatalogo("ESTADO_COMPUTADORA", "estado", "Seleccione el estado"),
        cargarCatalogo("TIPO_EQUIPO_TECNOLOGICO", "filtroTipo", "Todos los tipos"),
        cargarCatalogo("MARCA_EQUIPO_TECNOLOGICO", "filtroMarca", "Todas las marcas")
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
// CARGAR EQUIPOS / RENDERIZAR TABLA
// ========================================
async function cargarEquipos(url = "/api/equipos-tecnologicos") {
    try {
        const respuesta = await apiFetch(url);
        if (!respuesta || !respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "Error al cargar la lista de periféricos");
            alert(mensaje);
            return;
        }

        const equipos = await respuesta.json();
        renderizarTablaEquipos(equipos);
    } catch (error) {
        console.error(error);
        alert("Error al cargar la lista de periféricos");
    }
}

function renderizarTablaEquipos(equipos) {
    const tabla = document.getElementById("tablaEquipos");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!equipos || equipos.length === 0) {
        tabla.innerHTML = `<tr><td colspan="13" class="sin-datos">No se encontraron periféricos registrados.</td></tr>`;
        return;
    }

    equipos.forEach(eq => {
        const fila = document.createElement("tr");

        let badgeClase = "badge-info";
        const estadoNorm = (eq.estado || "").toUpperCase();
        if (estadoNorm.includes("BUENO") || estadoNorm.includes("OPERATIVO") || estadoNorm.includes("EXCELENTE")) {
            badgeClase = "badge-success";
        } else if (estadoNorm.includes("DANADO") || estadoNorm.includes("MALO") || estadoNorm.includes("BAJA")) {
            badgeClase = "badge-danger";
        } else if (estadoNorm.includes("REGULAR") || estadoNorm.includes("REVISION")) {
            badgeClase = "badge-warning";
        }

        const nombreFuncionario = eq.nombreFuncionario || "Sin asignar";
        const unidad = eq.unidadAdministrativaFuncionario ? esc(eq.unidadAdministrativaFuncionario) : "—";

        let botonEliminar = "";
        if (rol === "ADMIN") {
            botonEliminar = `
                <button type="button" class="btn-icon btn-icon-danger" title="Eliminar periférico" aria-label="Eliminar periférico" onclick="eliminarEquipo(${eq.id})">
                    🗑️
                </button>
            `;
        }

        fila.innerHTML = `
            <td><strong>${eq.id}</strong></td>
            <td>${esc(nombreFuncionario)}</td>
            <td>${unidad}</td>
            <td>${esc(eq.tipoEquipo || "—")}</td>
            <td>${esc(eq.marca || "—")}</td>
            <td>${esc(eq.modelo || "—")}</td>
            <td><strong>${esc(eq.serie || "—")}</strong></td>
            <td>${esc(eq.detalle || "—")}</td>
            <td><span class="badge ${badgeClase}">${esc(eq.estado || "OPERATIVO")}</span></td>
            <td>${esc(eq.etiquetaConstatacion || "—")}</td>
            <td>${esc(eq.nroPR || "—")}</td>
            <td>${esc(eq.ipTelefono || "—")}</td>
            <td style="text-align: center;">
                <div class="acciones-iconos" style="justify-content: center;">
                    <button type="button" class="btn-icon btn-icon-warning" title="Editar periférico" aria-label="Editar periférico" onclick="editarEquipo(${eq.id})">
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
async function buscarEquipos() {
    const cedula = document.getElementById("buscarCedula")?.value.trim();
    const serie = document.getElementById("buscarSerie")?.value.trim();
    const tipo = document.getElementById("filtroTipo")?.value.trim();
    const marca = document.getElementById("filtroMarca")?.value.trim();
    const etiqueta = document.getElementById("filtroEtiqueta")?.value.trim();

    if (!cedula && !serie && !tipo && !marca && !etiqueta) {
        await cargarEquipos();
        return;
    }

    const params = new URLSearchParams();
    if (cedula) params.append("cedula", cedula);
    if (serie) params.append("serie", serie);
    if (tipo) params.append("tipo", tipo);
    if (marca) params.append("marca", marca);
    if (etiqueta) params.append("etiqueta", etiqueta);

    const queryString = params.toString();
    const url = queryString ? `/api/equipos-tecnologicos?${queryString}` : "/api/equipos-tecnologicos";
    await cargarEquipos(url);
}

function limpiarBusqueda() {
    ["buscarCedula", "buscarSerie", "filtroTipo", "filtroMarca", "filtroEtiqueta"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = "";
    });
    cargarEquipos();
}

// ========================================
// ABRIR / CERRAR FORMULARIO
// ========================================
async function abrirFormulario() {
    modoEdicion = false;
    const form = document.getElementById("equipoForm");
    if (form) form.reset();

    document.getElementById("equipoId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nuevo periférico";
    document.getElementById("unidadAdministrativa").value = "";

    await cargarFuncionarios();
    await cargarTodosLosCatalogos();

    const panel = document.getElementById("formularioEquipo");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });

    setTimeout(() => {
        document.getElementById("tipoEquipo")?.focus();
    }, 250);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioEquipo");
    panel.style.display = "none";
    document.getElementById("equipoForm")?.reset();
}

// ========================================
// GUARDAR / ACTUALIZAR
// ========================================
document.getElementById("equipoForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("equipoId").value;
    const cedulaFuncionario = document.getElementById("funcionario").value || null;

    const datos = {
        cedulaFuncionario,
        tipoEquipo: obtenerValorCatalogo("tipoEquipo"),
        marca: obtenerValorCatalogo("marca"),
        modelo: document.getElementById("modelo").value.trim(),
        serie: document.getElementById("serie").value.trim(),
        detalle: document.getElementById("detalle").value.trim(),
        estado: obtenerValorCatalogo("estado"),
        etiquetaConstatacion: document.getElementById("etiquetaConstatacion").value.trim(),
        nroPR: document.getElementById("nroPR").value.trim(),
        ipTelefono: document.getElementById("ipTelefono").value.trim()
    };

    if (!datos.tipoEquipo || !datos.marca || !datos.estado) {
        alert("Por favor complete todos los campos obligatorios marcados con asterisco (*).");
        return;
    }

    try {
        let respuesta;
        if (!modoEdicion) {
            respuesta = await apiFetch("/api/equipos-tecnologicos", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/equipos-tecnologicos/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el periférico");
            alert(mensaje);
            return;
        }

        alert(modoEdicion ? "Periférico actualizado correctamente" : "Periférico creado correctamente");
        cerrarFormulario();
        await cargarEquipos();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ========================================
// EDITAR EQUIPO
// ========================================
async function editarEquipo(id) {
    try {
        const respuesta = await apiFetch(`/api/equipos-tecnologicos/${id}`);
        if (!respuesta || !respuesta.ok) throw new Error("Periférico no encontrado");

        const eq = await respuesta.json();
        modoEdicion = true;

        document.getElementById("equipoId").value = eq.id;
        document.getElementById("tituloFormulario").textContent = "Editar periférico";

        await cargarFuncionarios();
        await cargarTodosLosCatalogos();

        const cedula = eq.cedulaFuncionario || (eq.funcionario ? eq.funcionario.cedula : "");
        document.getElementById("funcionario").value = cedula;
        alSeleccionarFuncionario();

        document.getElementById("modelo").value = eq.modelo || "";
        document.getElementById("serie").value = eq.serie || "";
        document.getElementById("detalle").value = eq.detalle || "";
        document.getElementById("etiquetaConstatacion").value = eq.etiquetaConstatacion || "";
        document.getElementById("nroPR").value = eq.nroPR || "";
        document.getElementById("ipTelefono").value = eq.ipTelefono || "";

        seleccionarValorCatalogo("tipoEquipo", eq.tipoEquipo);
        seleccionarValorCatalogo("marca", eq.marca);
        seleccionarValorCatalogo("estado", eq.estado);

        const panel = document.getElementById("formularioEquipo");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });

        setTimeout(() => {
            document.getElementById("tipoEquipo")?.focus();
        }, 250);
    } catch (error) {
        console.error(error);
        alert("Error al cargar los datos del periférico");
    }
}

// ========================================
// ELIMINAR EQUIPO
// ========================================
async function eliminarEquipo(id) {
    if (!confirm("¿Está seguro de eliminar este periférico permanentemente?")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/equipos-tecnologicos/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar el periférico");
            alert(mensaje);
            return;
        }

        alert("Periférico eliminado correctamente");
        await cargarEquipos();
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
    await cargarEquipos();

    const idEdicion = new URLSearchParams(window.location.search).get("editar");
    if (/^\d+$/.test(idEdicion || "")) {
        window.history.replaceState({}, document.title, window.location.pathname);
        await editarEquipo(Number(idEdicion));
    }
}

iniciarPagina();