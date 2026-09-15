let modoEdicion = false;
let funcionariosCache = [];
let computadorasFuncionarioActual = [];

// ========================================
// JWT Y SESIÓN
// ========================================
const token = localStorage.getItem("token");
const rol = localStorage.getItem("rol");
const nombreUsuario = localStorage.getItem("nombre") || localStorage.getItem("usuario") || "Usuario del sistema";

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
// CARGAR CATÁLOGOS
// ========================================
async function cargarCatalogo(tipo, selectId, textoInicial, valorPorDefecto = "") {
    try {
        const respuesta = await apiFetch(`/api/catalogos/activos/${tipo}`);
        if (!respuesta || !respuesta.ok) return;

        const datos = await respuesta.json();
        const select = document.getElementById(selectId);
        if (!select) return;

        select.innerHTML = `<option value="">${textoInicial}</option>`;

        let encontradoDefecto = false;
        datos.forEach(item => {
            const opcion = document.createElement("option");
            opcion.value = item.nombre;
            opcion.textContent = item.nombre;
            if (valorPorDefecto && item.nombre.toLowerCase() === valorPorDefecto.toLowerCase()) {
                opcion.selected = true;
                encontradoDefecto = true;
            }
            select.appendChild(opcion);
        });

        if (valorPorDefecto && !encontradoDefecto) {
            const opcion = document.createElement("option");
            opcion.value = valorPorDefecto;
            opcion.textContent = valorPorDefecto;
            opcion.selected = true;
            select.appendChild(opcion);
        }
    } catch (error) {
        console.error(`Error cargando catálogo ${tipo}:`, error);
    }
}

async function cargarTodosLosCatalogos() {
    await Promise.all([
        cargarCatalogo("RESPONSABLE_MANTENIMIENTO", "responsable", "Seleccione el responsable", "Responsable asignado"),
        cargarCatalogo("TIPO_MANTENIMIENTO", "tipoMantenimiento", "Seleccione el tipo de mantenimiento"),
        cargarCatalogo("ESTADO_MANTENIMIENTO", "estadoMantenimiento", "Seleccione el estado del mantenimiento"),
        cargarCatalogo("ESTADO_POSTERIOR_COMPUTADORA", "estadoPosterior", "Seleccione el estado resultante")
    ]);
}

// ========================================
// PASO 1: BUSCADOR DE FUNCIONARIOS
// ========================================
let temporizadorBusqueda = null;
let funcionariosFiltrados = [];

function iniciarBuscadorFuncionario() {
    const input = document.getElementById("selectFuncionario");
    const lista = document.getElementById("listaFuncionarios");
    if (!input || !lista) return;

    input.addEventListener("input", function() {
        buscarFuncionarios(this.value);
    });

    input.addEventListener("focus", function() {
        if (this.value.trim()) {
            renderizarListaFuncionarios();
            lista.style.display = "block";
        }
    });

    document.addEventListener("click", function(event) {
        if (!event.target.closest(".combobox")) {
            lista.style.display = "none";
        }
    });
}

async function buscarFuncionarios(termino) {
    const lista = document.getElementById("listaFuncionarios");
    const terminoLimpio = (termino || "").trim();
    const funcionarioId = document.getElementById("funcionarioId");

    if (terminoLimpio.length < 2) {
        lista.style.display = "none";
        if (funcionarioId) funcionarioId.value = "";
        return;
    }

    clearTimeout(temporizadorBusqueda);
    temporizadorBusqueda = setTimeout(async () => {
        try {
            const respuesta = await apiFetch(`/api/funcionarios?buscar=${encodeURIComponent(terminoLimpio)}`);
            if (!respuesta || !respuesta.ok) return;

            funcionariosFiltrados = await respuesta.json();
            renderizarListaFuncionarios();
        } catch (error) {
            console.error("Error buscando funcionarios:", error);
        }
    }, 300);
}

function renderizarListaFuncionarios() {
    const lista = document.getElementById("listaFuncionarios");
    if (!lista) return;

    lista.innerHTML = "";

    if (!funcionariosFiltrados || funcionariosFiltrados.length === 0) {
        const vacio = document.createElement("div");
        vacio.className = "combobox-item combobox-vacio";
        vacio.textContent = "No se encontraron funcionarios";
        lista.appendChild(vacio);
        lista.style.display = "block";
        return;
    }

    funcionariosFiltrados.forEach(f => {
        const item = document.createElement("div");
        item.className = "combobox-item";
        const nombres = f.nombres || f.nombrePila || "";
        const apellidos = f.apellidos || "";
        item.textContent = `${f.cedula} - ${nombres} ${apellidos} (${f.unidadAdministrativa || "Sin área"})`.trim();
        item.addEventListener("mousedown", function(event) {
            event.preventDefault();
            seleccionarFuncionario(f);
        });
        lista.appendChild(item);
    });

    lista.style.display = "block";
}

function seleccionarFuncionario(f) {
    const input = document.getElementById("selectFuncionario");
    const funcionarioId = document.getElementById("funcionarioId");
    if (!input || !funcionarioId) return;

    const nombres = f.nombres || f.nombrePila || "";
    const apellidos = f.apellidos || "";
    input.value = `${f.cedula} - ${nombres} ${apellidos}`.trim();
    funcionarioId.value = f.id;
    document.getElementById("listaFuncionarios").style.display = "none";

    agregarFuncionarioACache(f);
    alCambiarFuncionario();
}

function agregarFuncionarioACache(f) {
    const indice = funcionariosCache.findIndex(item => String(item.id) === String(f.id));
    if (indice >= 0) {
        funcionariosCache[indice] = f;
    } else {
        funcionariosCache.push(f);
    }
}

async function cargarFuncionarioPorId(id) {
    if (!id) return null;
    const existente = funcionariosCache.find(f => String(f.id) === String(id));
    if (existente) return existente;
    try {
        const respuesta = await apiFetch(`/api/funcionarios/${id}`);
        if (respuesta && respuesta.ok) {
            const f = await respuesta.json();
            agregarFuncionarioACache(f);
            return f;
        }
    } catch (error) {
        console.error("Error cargando funcionario:", error);
    }
    return null;
}

// ========================================
// PASO 2: AL SELECCIONAR FUNCIONARIO
// ========================================
async function alCambiarFuncionario() {
    const funcionarioId = document.getElementById("funcionarioId")?.value;
    const selectSerie = document.getElementById("selectPcSerie");
    const selectNombre = document.getElementById("selectPcNombre");
    const infoCard = document.getElementById("infoComputadoraCard");

    selectSerie.innerHTML = `<option value="">Seleccione el número de serie...</option>`;
    selectNombre.innerHTML = `<option value="">Seleccione el nombre de equipo...</option>`;
    document.getElementById("computadoraId").value = "";
    infoCard.style.display = "none";
    computadorasFuncionarioActual = [];

    if (!funcionarioId) return;

    try {
        const respuesta = await apiFetch(`/api/computadoras/funcionario/${funcionarioId}`);
        if (!respuesta || !respuesta.ok) return;

        computadorasFuncionarioActual = await respuesta.json();

        if (computadorasFuncionarioActual.length === 0) {
            selectSerie.innerHTML = `<option value="">Este funcionario no tiene computadoras asignadas</option>`;
            selectNombre.innerHTML = `<option value="">Este funcionario no tiene computadoras asignadas</option>`;
            return;
        }

        computadorasFuncionarioActual.forEach(pc => {
            const optSerie = document.createElement("option");
            optSerie.value = pc.id;
            optSerie.textContent = `${pc.serie} (${pc.marca || ""} ${pc.modelo || ""})`;
            selectSerie.appendChild(optSerie);

            const optNombre = document.createElement("option");
            optNombre.value = pc.id;
            optNombre.textContent = `${pc.nombreEquipo} - ${pc.serie}`;
            selectNombre.appendChild(optNombre);
        });

        // Si tiene 1 sola computadora, autoseleccionarla
        if (computadorasFuncionarioActual.length === 1) {
            const singlePc = computadorasFuncionarioActual[0];
            selectSerie.value = singlePc.id;
            selectNombre.value = singlePc.id;
            mostrarDetallesComputadora(singlePc);
        }
    } catch (error) {
        console.error("Error al cargar computadoras del funcionario:", error);
    }
}

function alCambiarPcPorSerie() {
    const pcId = document.getElementById("selectPcSerie").value;
    document.getElementById("selectPcNombre").value = pcId;
    sincronizarComputadoraSeleccionada(pcId);
}

function alCambiarPcPorNombre() {
    const pcId = document.getElementById("selectPcNombre").value;
    document.getElementById("selectPcSerie").value = pcId;
    sincronizarComputadoraSeleccionada(pcId);
}

function sincronizarComputadoraSeleccionada(pcId) {
    if (!pcId) {
        document.getElementById("computadoraId").value = "";
        document.getElementById("infoComputadoraCard").style.display = "none";
        return;
    }

    const pc = computadorasFuncionarioActual.find(p => String(p.id) === String(pcId));
    if (pc) {
        mostrarDetallesComputadora(pc);
    }
}

// ========================================
// PASO 3: MOSTRAR DATOS AUTOMÁTICOS DE COMPUTADORA
// ========================================
function mostrarDetallesComputadora(pc) {
    document.getElementById("computadoraId").value = pc.id;

    const funcionarioId = document.getElementById("funcionarioId").value;
    const funcionario = funcionariosCache.find(f => String(f.id) === String(funcionarioId));

    document.getElementById("infoFuncionario").textContent = funcionario ? `${funcionario.nombres || funcionario.nombrePila} ${funcionario.apellidos || ""}`.trim() : (pc.nombreFuncionario || "—");
    document.getElementById("infoCedula").textContent = (funcionario && funcionario.cedula) ? funcionario.cedula : (pc.cedulaFuncionario || "—");
    document.getElementById("infoUnidad").textContent = funcionario ? (funcionario.unidadAdministrativa || "—") : (pc.unidadAdministrativaFuncionario || "—");
    document.getElementById("infoNombreEquipo").textContent = pc.nombreEquipo || "—";
    document.getElementById("infoSerie").textContent = pc.serie || "—";
    document.getElementById("infoMarcaModelo").textContent = `${pc.marca || "—"} / ${pc.modelo || "—"}`;
    document.getElementById("infoTipo").textContent = pc.tipo || "—";
    document.getElementById("infoUbicacion").textContent = pc.ubicacion || "—";
    document.getElementById("infoEstadoActual").textContent = pc.estado || "OPERATIVO";

    document.getElementById("infoComputadoraCard").style.display = "block";
}

// ========================================
// CARGAR MANTENIMIENTOS / TABLA
// ========================================
async function cargarMantenimientos(url = "/api/mantenimientos") {
    try {
        const respuesta = await apiFetch(url);
        if (!respuesta || !respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "Error al cargar la lista de mantenimientos");
            alert(mensaje);
            return;
        }

        const mantenimientos = await respuesta.json();
        renderizarTablaMantenimientos(mantenimientos);
    } catch (error) {
        console.error(error);
        alert("Error al cargar la lista de mantenimientos");
    }
}

function renderizarTablaMantenimientos(mantenimientos) {
    const tabla = document.getElementById("tablaMantenimientos");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!mantenimientos || mantenimientos.length === 0) {
        tabla.innerHTML = `<tr><td colspan="10" class="sin-datos">No se encontraron mantenimientos registrados.</td></tr>`;
        return;
    }

    mantenimientos.forEach(m => {
        const fila = document.createElement("tr");

        let badgeClase = "badge-info";
        const estadoNorm = (m.estadoMantenimiento || "").toUpperCase();
        if (estadoNorm.includes("COMPLETADO") || estadoNorm.includes("FINALIZADO") || estadoNorm.includes("EXITOSO")) {
            badgeClase = "badge-success";
        } else if (estadoNorm.includes("CANCELADO") || estadoNorm.includes("FALLIDO")) {
            badgeClase = "badge-danger";
        } else if (estadoNorm.includes("PROCESO") || estadoNorm.includes("PENDIENTE")) {
            badgeClase = "badge-warning";
        }

        const fechaStr = m.fechaMantenimiento ? m.fechaMantenimiento.replace("T", " ").substring(0, 16) : "—";
        const funcionarioOEquipo = m.nombreFuncionario ? `${m.nombreFuncionario} (${m.nombreEquipo || "PC"})` : (m.nombreEquipo || "PC");

        let botonEliminar = "";
        if (rol === "ADMIN") {
            botonEliminar = `
                <button type="button" class="btn-icon btn-icon-danger" title="Eliminar mantenimiento" aria-label="Eliminar mantenimiento" onclick="eliminarMantenimiento(${m.id})">
                    🗑️
                </button>
            `;
        }

        fila.innerHTML = `
            <td><strong>${m.id}</strong></td>
            <td>${funcionarioOEquipo}</td>
            <td><strong>${m.serie || "—"}</strong></td>
            <td>${m.nombreResponsable || "Responsable asignado"}</td>
            <td>${fechaStr}</td>
            <td>${m.tipoMantenimiento || "—"}</td>
            <td><span class="badge ${badgeClase}">${m.estadoMantenimiento || "COMPLETADO"}</span></td>
            <td>$${m.costo ? Number(m.costo).toFixed(2) : "0.00"}</td>
            <td><span class="badge badge-success">${m.estadoPosterior || "OPERATIVO"}</span></td>
            <td style="text-align: center;">
                <div class="acciones-iconos" style="justify-content: center;">
                    <button type="button" class="btn-icon btn-icon-warning" title="Editar mantenimiento" aria-label="Editar mantenimiento" onclick="editarMantenimiento(${m.id})">
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
async function buscarMantenimientos() {
    const cedula = document.getElementById("buscarCedula")?.value.trim();
    const serie = document.getElementById("buscarSerie")?.value.trim();

    const params = new URLSearchParams();
    if (cedula) params.append("cedula", cedula);
    if (serie) params.append("serie", serie);

    const queryString = params.toString();
    const url = queryString ? `/api/mantenimientos?${queryString}` : "/api/mantenimientos";
    await cargarMantenimientos(url);
}

function limpiarBusqueda() {
    if (document.getElementById("buscarCedula")) document.getElementById("buscarCedula").value = "";
    if (document.getElementById("buscarSerie")) document.getElementById("buscarSerie").value = "";
    cargarMantenimientos();
}

// ========================================
// ABRIR / CERRAR FORMULARIO
// ========================================
async function abrirFormulario() {
    modoEdicion = false;
    const form = document.getElementById("mantenimientoForm");
    if (form) form.reset();

    document.getElementById("mantenimientoId").value = "";
    document.getElementById("computadoraId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nuevo mantenimiento";
    document.getElementById("usuarioRegistrador").value = nombreUsuario;
    document.getElementById("infoComputadoraCard").style.display = "none";

    // Set default datetime to now
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    document.getElementById("fechaMantenimiento").value = now.toISOString().slice(0, 16);
    document.getElementById("costo").value = "0.00";

    const inputFuncionario = document.getElementById("selectFuncionario");
    if (inputFuncionario) inputFuncionario.value = "";
    document.getElementById("funcionarioId").value = "";
    document.getElementById("listaFuncionarios").style.display = "none";
    document.getElementById("selectPcSerie").innerHTML = `<option value="">Seleccione el número de serie...</option>`;
    document.getElementById("selectPcNombre").innerHTML = `<option value="">Seleccione el nombre de equipo...</option>`;

    await cargarTodosLosCatalogos();

    const panel = document.getElementById("formularioMantenimiento");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });

    setTimeout(() => {
        document.getElementById("selectFuncionario")?.focus();
    }, 250);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioMantenimiento");
    panel.style.display = "none";
    document.getElementById("mantenimientoForm")?.reset();
    document.getElementById("funcionarioId").value = "";
    document.getElementById("infoComputadoraCard").style.display = "none";
    document.getElementById("listaFuncionarios").style.display = "none";
}

// ========================================
// GUARDAR / ACTUALIZAR
// ========================================
document.getElementById("mantenimientoForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("mantenimientoId").value;
    const computadoraId = document.getElementById("computadoraId").value;

    if (!computadoraId) {
        const funcionarioSeleccionado = document.getElementById("funcionarioId").value;
        if (!funcionarioSeleccionado) {
            alert("Por favor busque y seleccione un funcionario.");
            document.getElementById("selectFuncionario")?.focus();
        } else {
            alert("Por favor seleccione la computadora del funcionario (por serie o nombre de equipo).");
            document.getElementById("selectPcSerie")?.focus();
        }
        return;
    }

    const responsable = document.getElementById("responsable").value.trim() || "Responsable asignado";
    const tipoMantenimiento = document.getElementById("tipoMantenimiento").value;
    const estadoMantenimiento = document.getElementById("estadoMantenimiento").value;
    const estadoPosterior = document.getElementById("estadoPosterior").value;
    const fechaMantenimiento = document.getElementById("fechaMantenimiento").value;
    const diagnostico = document.getElementById("diagnostico").value.trim();
    const trabajoRealizado = document.getElementById("trabajoRealizado").value.trim();
    const costo = document.getElementById("costo").value || "0.00";
    const observaciones = document.getElementById("observaciones").value.trim();

    if (!tipoMantenimiento || !estadoMantenimiento || !estadoPosterior || !diagnostico || !trabajoRealizado) {
        alert("Por favor complete todos los campos obligatorios marcados con asterisco (*).");
        return;
    }

    const datos = {
        computadoraId: Number(computadoraId),
        responsable: responsable,
        fechaMantenimiento,
        tipoMantenimiento,
        estadoMantenimiento,
        estadoPosterior,
        diagnostico,
        trabajoRealizado,
        costo: Number(costo),
        observaciones
    };

    try {
        let respuesta;
        if (!modoEdicion) {
            respuesta = await apiFetch("/api/mantenimientos", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/mantenimientos/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el mantenimiento");
            alert(mensaje);
            return;
        }

        alert(modoEdicion ? "Mantenimiento actualizado correctamente" : "Mantenimiento registrado y estado de computadora actualizado correctamente");
        cerrarFormulario();
        await cargarMantenimientos();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ========================================
// EDITAR MANTENIMIENTO
// ========================================
async function editarMantenimiento(id) {
    try {
        const respuesta = await apiFetch(`/api/mantenimientos/${id}`);
        if (!respuesta || !respuesta.ok) throw new Error("Mantenimiento no encontrado");

        const m = await respuesta.json();
        modoEdicion = true;

        document.getElementById("mantenimientoId").value = m.id;
        document.getElementById("computadoraId").value = m.computadoraId;
        document.getElementById("tituloFormulario").textContent = "Editar mantenimiento";
        document.getElementById("usuarioRegistrador").value = m.usuarioRegistrador || nombreUsuario;

        await cargarTodosLosCatalogos();

        if (m.fechaMantenimiento) {
            document.getElementById("fechaMantenimiento").value = m.fechaMantenimiento.slice(0, 16);
        }
        document.getElementById("diagnostico").value = m.diagnostico || "";
        document.getElementById("trabajoRealizado").value = m.trabajoRealizado || "";
        document.getElementById("costo").value = m.costo || "0.00";
        document.getElementById("observaciones").value = m.observaciones || "";

        document.getElementById("responsable").value = m.nombreResponsable || "Responsable asignado";
        document.getElementById("tipoMantenimiento").value = m.tipoMantenimiento || "";
        document.getElementById("estadoMantenimiento").value = m.estadoMantenimiento || "";
        document.getElementById("estadoPosterior").value = m.estadoPosterior || "OPERATIVO";

        // Cargar datos de la computadora asociada
        const pcResp = await apiFetch(`/api/computadoras/${m.computadoraId}`);
        if (pcResp && pcResp.ok) {
            const pc = await pcResp.json();
            const funcId = pc.funcionarioId || (pc.funcionario ? pc.funcionario.id : "");
            if (funcId) {
                document.getElementById("funcionarioId").value = funcId;
                await cargarFuncionarioPorId(funcId);
                const inputFunc = document.getElementById("selectFuncionario");
                if (inputFunc) {
                    const func = funcionariosCache.find(f => String(f.id) === String(funcId));
                    inputFunc.value = func
                        ? `${func.cedula} - ${func.nombres || func.nombrePila || ""} ${func.apellidos || ""}`.trim()
                        : (m.nombreFuncionario || pc.nombreFuncionario || "");
                }
                document.getElementById("listaFuncionarios").style.display = "none";
                await alCambiarFuncionario();
                document.getElementById("selectPcSerie").value = pc.id;
                document.getElementById("selectPcNombre").value = pc.id;
            }
            mostrarDetallesComputadora(pc);
        }

        const panel = document.getElementById("formularioMantenimiento");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });

        setTimeout(() => {
            document.getElementById("diagnostico")?.focus();
        }, 250);
    } catch (error) {
        console.error(error);
        alert("Error al cargar los datos del mantenimiento");
    }
}

// ========================================
// ELIMINAR MANTENIMIENTO
// ========================================
async function eliminarMantenimiento(id) {
    if (!confirm("¿Está seguro de eliminar este registro de mantenimiento?")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/mantenimientos/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar el mantenimiento");
            alert(mensaje);
            return;
        }

        alert("Mantenimiento eliminado correctamente");
        await cargarMantenimientos();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
}

// ========================================
// INICIALIZACIÓN
// ========================================
async function iniciarPagina() {
    iniciarBuscadorFuncionario();
    await cargarTodosLosCatalogos();
    await cargarMantenimientos();

    const idEdicion = new URLSearchParams(window.location.search).get("editar");
    if (/^\d+$/.test(idEdicion || "")) {
        window.history.replaceState({}, document.title, window.location.pathname);
        await editarMantenimiento(Number(idEdicion));
    }
}

iniciarPagina();
