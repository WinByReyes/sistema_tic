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
        if (!respuesta || !respuesta.ok) return;

        const datos = await respuesta.json();
        const select = document.getElementById(selectId);
        if (!select) return;

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
    } catch (error) {
        console.error(`Error cargando catálogo ${tipo}:`, error);
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
        cargarCatalogo("TIPO_EQUIPO", "tipo", "Seleccione el tipo de equipo"),
        cargarCatalogo("MARCA", "marca", "Seleccione la marca"),
        cargarCatalogo("UBICACION", "ubicacion", "Seleccione la ubicación"),
        cargarCatalogo("ESTADO_COMPUTADORA", "estado", "Seleccione el estado"),
        cargarCatalogo("TIPO_PROCESADOR", "tipoProcesador", "Seleccione el procesador"),
        cargarCatalogo("GENERACION_PROCESADOR", "generacionProcesador", "Seleccione la generación"),
        cargarCatalogo("RAM", "memoriaRAM", "Seleccione la memoria RAM"),
        cargarCatalogo("TIPO_DISCO", "tipoDisco", "Seleccione el tipo de disco"),
        cargarCatalogo("SISTEMA_OPERATIVO", "sistemaOperativo", "Seleccione el sistema operativo"),
        cargarCatalogo("OFIMATICA", "office", "Seleccione paquete ofimático"),
        cargarCatalogo("ANTIVIRUS", "antivirus", "Seleccione el antivirus")
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
// CARGAR COMPUTADORAS / RENDERIZAR TABLA
// ========================================
async function cargarComputadoras(url = "/api/computadoras") {
    try {
        const respuesta = await apiFetch(url);
        if (!respuesta || !respuesta.ok) throw new Error("No se pudieron cargar las computadoras");

        const computadoras = await respuesta.json();
        renderizarTablaComputadoras(computadoras);
    } catch (error) {
        console.error(error);
        alert("Error al cargar la lista de computadoras");
    }
}

function renderizarTablaComputadoras(computadoras) {
    const tabla = document.getElementById("tablaComputadoras");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!computadoras || computadoras.length === 0) {
        tabla.innerHTML = `<tr><td colspan="10" class="sin-datos">No se encontraron computadoras registradas.</td></tr>`;
        return;
    }

    computadoras.forEach(pc => {
        const fila = document.createElement("tr");

        let badgeClase = "badge-info";
        const estadoNorm = (pc.estado || "").toUpperCase();
        if (estadoNorm.includes("BUENO") || estadoNorm.includes("OPERATIVO") || estadoNorm.includes("EXCELENTE")) {
            badgeClase = "badge-success";
        } else if (estadoNorm.includes("DANADO") || estadoNorm.includes("MALO") || estadoNorm.includes("BAJA")) {
            badgeClase = "badge-danger";
        } else if (estadoNorm.includes("REGULAR") || estadoNorm.includes("REVISION")) {
            badgeClase = "badge-warning";
        }

        const nombreFuncionario = pc.nombreFuncionario || "Sin asignar";

        let botonEliminar = "";
        if (rol === "ADMIN") {
            botonEliminar = `
                <button type="button" class="btn-icon btn-icon-danger" title="Eliminar computadora" aria-label="Eliminar computadora" onclick="eliminarComputadora(${pc.id})">
                    🗑️
                </button>
            `;
        }

        fila.innerHTML = `
            <td><strong>${pc.id}</strong></td>
            <td>${nombreFuncionario}</td>
            <td><strong>${pc.serie || "—"}</strong></td>
            <td>${pc.nombreEquipo || "—"}</td>
            <td>${pc.tipo || "—"}</td>
            <td>${pc.marca || "—"}</td>
            <td>${pc.modelo || "—"}</td>
            <td><span class="badge ${badgeClase}">${pc.estado || "OPERATIVO"}</span></td>
            <td>${pc.ubicacion || "—"}</td>
            <td style="text-align: center;">
                <div class="acciones-iconos" style="justify-content: center;">
                    <button type="button" class="btn-icon btn-icon-warning" title="Editar computadora" aria-label="Editar computadora" onclick="editarComputadora(${pc.id})">
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
// BÚSQUEDA DUAL (CÉDULA Y/O SERIE)
// ========================================
async function buscarComputadoras() {
    const cedula = document.getElementById("buscarCedula")?.value.trim();
    const serie = document.getElementById("buscarSerie")?.value.trim();

    const params = new URLSearchParams();
    if (cedula) params.append("cedula", cedula);
    if (serie) params.append("serie", serie);

    const queryString = params.toString();
    const url = queryString ? `/api/computadoras?${queryString}` : "/api/computadoras";
    await cargarComputadoras(url);
}

function limpiarBusqueda() {
    if (document.getElementById("buscarCedula")) document.getElementById("buscarCedula").value = "";
    if (document.getElementById("buscarSerie")) document.getElementById("buscarSerie").value = "";
    cargarComputadoras();
}

// ========================================
// ABRIR / CERRAR FORMULARIO
// ========================================
async function abrirFormulario() {
    modoEdicion = false;
    const form = document.getElementById("computadoraForm");
    if (form) form.reset();

    document.getElementById("computadoraId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nueva computadora";
    document.getElementById("unidadAdministrativa").value = "";

    await cargarFuncionarios();
    await cargarTodosLosCatalogos();

    const panel = document.getElementById("formularioComputadora");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });

    setTimeout(() => {
        document.getElementById("funcionario")?.focus();
    }, 250);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioComputadora");
    panel.style.display = "none";
    document.getElementById("computadoraForm")?.reset();
}

// ========================================
// GUARDAR / ACTUALIZAR
// ========================================
document.getElementById("computadoraForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("computadoraId").value;
    const cedulaFuncionario = document.getElementById("funcionario").value || null;

    const datos = {
        cedulaFuncionario,
        serie: document.getElementById("serie").value.trim(),
        nombreEquipo: document.getElementById("nombreEquipo").value.trim(),
        procedencia: "Institucional",
        tipo: obtenerValorCatalogo("tipo"),
        marca: obtenerValorCatalogo("marca"),
        modelo: document.getElementById("modelo").value.trim(),
        ubicacion: obtenerValorCatalogo("ubicacion"),
        estado: obtenerValorCatalogo("estado"),

        tipoProcesador: obtenerValorCatalogo("tipoProcesador"),
        generacionProcesador: obtenerValorCatalogo("generacionProcesador"),
        velocidadProcesador: document.getElementById("velocidadProcesador").value.trim() || "N/A",
        memoriaRAM: obtenerValorCatalogo("memoriaRAM"),
        tipoDisco: obtenerValorCatalogo("tipoDisco"),
        capacidadDiscoGB: document.getElementById("capacidadDiscoGB").value,

        sistemaOperativo: obtenerValorCatalogo("sistemaOperativo"),
        office: obtenerValorCatalogo("office") || "N/A",
        antivirus: obtenerValorCatalogo("antivirus") || "N/A",
        observacionSoftware: document.getElementById("observacionSoftware").value.trim(),

        ip: document.getElementById("ip").value.trim(),
        macLan: document.getElementById("macLan").value.trim(),
        macWifi: document.getElementById("macWifi").value.trim(),
        nroPR: document.getElementById("nroPR").value.trim(),
        observacionRed: document.getElementById("observacionRed").value.trim()
    };

    if (!datos.tipo || !datos.marca || !datos.tipoProcesador || !datos.generacionProcesador || !datos.memoriaRAM || !datos.tipoDisco || !datos.sistemaOperativo || !datos.ubicacion || !datos.estado) {
        alert("Por favor complete todos los campos obligatorios marcados con asterisco (*).");
        return;
    }

    try {
        let respuesta;
        if (!modoEdicion) {
            respuesta = await apiFetch("/api/computadoras", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/computadoras/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar la computadora");
            alert(mensaje);
            return;
        }

        alert(modoEdicion ? "Computadora actualizada correctamente" : "Computadora creada correctamente");
        cerrarFormulario();
        await cargarComputadoras();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ========================================
// EDITAR COMPUTADORA
// ========================================
async function editarComputadora(id) {
    try {
        const respuesta = await apiFetch(`/api/computadoras/${id}`);
        if (!respuesta || !respuesta.ok) throw new Error("Computadora no encontrada");

        const pc = await respuesta.json();
        modoEdicion = true;

        document.getElementById("computadoraId").value = pc.id;
        document.getElementById("tituloFormulario").textContent = "Editar computadora";

        await cargarFuncionarios();
        await cargarTodosLosCatalogos();

        const cedula = pc.cedulaFuncionario || (pc.funcionario ? pc.funcionario.cedula : "");
        document.getElementById("funcionario").value = cedula;
        alSeleccionarFuncionario();

        document.getElementById("serie").value = pc.serie || "";
        document.getElementById("nombreEquipo").value = pc.nombreEquipo || "";
        document.getElementById("modelo").value = pc.modelo || "";
        document.getElementById("velocidadProcesador").value = pc.velocidadProcesador || "";
        document.getElementById("capacidadDiscoGB").value = pc.capacidadDiscoGB || "";

        document.getElementById("observacionSoftware").value = pc.observacionSoftware || "";
        document.getElementById("ip").value = pc.ip || "";
        document.getElementById("macLan").value = pc.macLan || "";
        document.getElementById("macWifi").value = pc.macWifi || "";
        document.getElementById("nroPR").value = pc.nroPR || "";
        document.getElementById("observacionRed").value = pc.observacionRed || "";

        seleccionarValorCatalogo("tipo", pc.tipo);
        seleccionarValorCatalogo("marca", pc.marca);
        seleccionarValorCatalogo("ubicacion", pc.ubicacion);
        seleccionarValorCatalogo("estado", pc.estado);
        seleccionarValorCatalogo("tipoProcesador", pc.tipoProcesador);
        seleccionarValorCatalogo("generacionProcesador", pc.generacionProcesador);
        seleccionarValorCatalogo("memoriaRAM", pc.memoriaRAM);
        seleccionarValorCatalogo("tipoDisco", pc.tipoDisco);
        seleccionarValorCatalogo("sistemaOperativo", pc.sistemaOperativo);
        seleccionarValorCatalogo("office", pc.office || pc.ofimatica);
        seleccionarValorCatalogo("antivirus", pc.antivirus);

        const panel = document.getElementById("formularioComputadora");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });

        setTimeout(() => {
            document.getElementById("serie")?.focus();
        }, 250);
    } catch (error) {
        console.error(error);
        alert("Error al cargar los datos de la computadora");
    }
}

// ========================================
// ELIMINAR COMPUTADORA
// ========================================
async function eliminarComputadora(id) {
    if (!confirm("¿Está seguro de eliminar esta computadora permanentemente?")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/computadoras/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar la computadora");
            alert(mensaje);
            return;
        }

        alert("Computadora eliminada correctamente");
        await cargarComputadoras();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
}

// ========================================
// INICIO
// ========================================
async function iniciarPagina() {
    await cargarFuncionarios();
    await cargarTodosLosCatalogos();
    await cargarComputadoras();

    const idEdicion = new URLSearchParams(window.location.search).get("editar");
    if (/^\d+$/.test(idEdicion || "")) {
        window.history.replaceState({}, document.title, window.location.pathname);
        await editarComputadora(Number(idEdicion));
    }
}

iniciarPagina();
