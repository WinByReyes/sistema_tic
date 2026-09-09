let modoEdicion = false;

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
// VALIDACIÓN DE CÉDULA ECUATORIANA
// ========================================
function validarCedulaEcuatoriana(cedula) {
    if (!cedula || cedula.length !== 10 || !/^\d{10}$/.test(cedula)) {
        return false;
    }
    const provincia = parseInt(cedula.substring(0, 2), 10);
    if ((provincia < 1 || provincia > 24) && provincia !== 30) {
        return false;
    }
    const tercerDigito = parseInt(cedula.charAt(2), 10);
    if (tercerDigito >= 6) {
        return false;
    }
    const coeficientes = [2, 1, 2, 1, 2, 1, 2, 1, 2];
    let suma = 0;
    for (let i = 0; i < 9; i++) {
        let valor = parseInt(cedula.charAt(i), 10) * coeficientes[i];
        if (valor >= 10) valor -= 9;
        suma += valor;
    }
    const digitoVerificador = (10 - (suma % 10)) % 10;
    return digitoVerificador === parseInt(cedula.charAt(9), 10);
}

// ========================================
// CARGAR CATÁLOGOS (UNIDAD / CARGO)
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
    } catch (error) {
        console.error(`Error cargando catálogo ${tipo}:`, error);
    }
}

async function cargarCatalogosFormulario() {
    await Promise.all([
        cargarCatalogo("UNIDAD_ADMINISTRATIVA", "unidadAdministrativa", "Seleccione la unidad administrativa"),
        cargarCatalogo("CARGO", "cargo", "Seleccione el cargo")
    ]);
}

// Si el valor guardado no está en el catálogo (dato histórico), se agrega
// como opción para no perderlo al editar.
function seleccionarValorOAgregar(selectId, valor) {
    const select = document.getElementById(selectId);
    if (!select) return;
    if (!valor) {
        select.value = "";
        return;
    }
    const existe = [...select.options].some(op => op.value === valor);
    if (!existe) {
        const opcion = document.createElement("option");
        opcion.value = valor;
        opcion.textContent = valor;
        select.appendChild(opcion);
    }
    select.value = valor;
}

// ========================================
// CARGAR FUNCIONARIOS
// ========================================
async function cargarFuncionarios(url = "/api/funcionarios") {
    try {
        const respuesta = await apiFetch(url);
        if (!respuesta) return;

        if (!respuesta.ok) {
            throw new Error("No se pudieron cargar los funcionarios");
        }

        const funcionarios = await respuesta.json();
        renderizarTabla(funcionarios);
    } catch (error) {
        console.error(error);
        alert("Error al cargar los funcionarios");
    }
}

function renderizarTabla(funcionarios) {
    const tabla = document.getElementById("tablaFuncionarios");
    if (!tabla) return;
    tabla.innerHTML = "";

    if (!funcionarios || funcionarios.length === 0) {
        tabla.innerHTML = `<tr><td colspan="9" class="sin-datos">No se encontraron funcionarios registrados.</td></tr>`;
        return;
    }

    funcionarios.forEach(funcionario => {
        const fila = document.createElement("tr");

        let badgeClase = "badge-gray";
        if (funcionario.estado === "ACTIVO") badgeClase = "badge-success";
        else if (funcionario.estado === "INACTIVO" || funcionario.estado === "CESADO") badgeClase = "badge-danger";
        else if (funcionario.estado === "JUBILADO") badgeClase = "badge-warning";

        const nombres = funcionario.nombres || funcionario.nombrePila || "—";
        const apellidos = funcionario.apellidos || "";

        fila.innerHTML = `
            <td><strong>${funcionario.id}</strong></td>
            <td>${funcionario.cedula || "—"}</td>
            <td>${nombres}</td>
            <td>${apellidos}</td>
            <td>${funcionario.unidadAdministrativa || "—"}</td>
            <td>${funcionario.cargo || "—"}</td>
            <td><span class="badge ${badgeClase}">${funcionario.estado || "ACTIVO"}</span></td>
            <td>${funcionario.codigoBiometrico || "—"}</td>
            <td style="text-align: center;">
                <div class="acciones-iconos" style="justify-content: center;">
                    <button type="button" class="btn-icon btn-icon-warning" title="Editar funcionario" aria-label="Editar funcionario" onclick="editarFuncionario(${funcionario.id})">
                        ✏️
                    </button>
                    <button type="button" class="btn-icon btn-icon-danger" title="Desactivar funcionario" aria-label="Desactivar funcionario" onclick="eliminarFuncionario(${funcionario.id})">
                        ⛔
                    </button>
                </div>
            </td>
        `;

        tabla.appendChild(fila);
    });
}

// ========================================
// BÚSQUEDA
// ========================================
async function buscarFuncionarios() {
    const cedula = document.getElementById("buscarCedula")?.value.trim();
    const nombre = document.getElementById("buscarNombre")?.value.trim();

    if (cedula) {
        await cargarFuncionarios(`/api/funcionarios?cedula=${encodeURIComponent(cedula)}`);
    } else if (nombre) {
        await cargarFuncionarios(`/api/funcionarios?buscar=${encodeURIComponent(nombre)}`);
    } else {
        await cargarFuncionarios();
    }
}

function limpiarBusqueda() {
    if (document.getElementById("buscarCedula")) document.getElementById("buscarCedula").value = "";
    if (document.getElementById("buscarNombre")) document.getElementById("buscarNombre").value = "";
    cargarFuncionarios();
}

// ========================================
// ABRIR / CERRAR FORMULARIO
// ========================================
function abrirFormulario() {
    modoEdicion = false;
    const form = document.getElementById("funcionarioForm");
    if (form) form.reset();

    document.getElementById("funcionarioId").value = "";
    document.getElementById("tituloFormulario").textContent = "Nuevo funcionario";
    document.getElementById("cedula").readOnly = false;
    document.getElementById("cedulaError").style.display = "none";

    const panel = document.getElementById("formularioFuncionario");
    panel.style.display = "block";
    panel.scrollIntoView({ behavior: "smooth", block: "start" });

    setTimeout(() => {
        document.getElementById("cedula")?.focus();
    }, 250);
}

function cerrarFormulario() {
    const panel = document.getElementById("formularioFuncionario");
    panel.style.display = "none";
    document.getElementById("funcionarioForm")?.reset();
}

// ========================================
// GUARDAR / ACTUALIZAR
// ========================================
document.getElementById("funcionarioForm")?.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("funcionarioId").value;
    const cedula = document.getElementById("cedula").value.trim();
    const nombres = document.getElementById("nombres").value.trim();
    const apellidos = document.getElementById("apellidos").value.trim();
    const unidadAdministrativa = document.getElementById("unidadAdministrativa").value.trim();
    const cargo = document.getElementById("cargo").value.trim();
    const estado = document.getElementById("estado").value;
    const codigoBiometrico = document.getElementById("codigoBiometrico").value.trim();

    if (!validarCedulaEcuatoriana(cedula)) {
        document.getElementById("cedulaError").style.display = "block";
        document.getElementById("cedula").focus();
        alert("La cédula ingresada no es válida para Ecuador.");
        return;
    } else {
        document.getElementById("cedulaError").style.display = "none";
    }

    const datos = {
        cedula,
        nombres,
        apellidos,
        nombrePila: `${nombres} ${apellidos}`.trim(),
        unidadAdministrativa,
        cargo,
        estado,
        codigoBiometrico
    };

    try {
        let respuesta;
        if (!modoEdicion) {
            respuesta = await apiFetch("/api/funcionarios", {
                method: "POST",
                body: JSON.stringify(datos)
            });
        } else {
            respuesta = await apiFetch(`/api/funcionarios/${id}`, {
                method: "PUT",
                body: JSON.stringify(datos)
            });
        }

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el funcionario");
            alert(mensaje);
            return;
        }

        alert(modoEdicion ? "Funcionario actualizado correctamente" : "Funcionario creado correctamente");
        cerrarFormulario();
        await cargarFuncionarios();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
});

// ========================================
// EDITAR
// ========================================
async function editarFuncionario(id) {
    try {
        const respuesta = await apiFetch(`/api/funcionarios/${id}`);
        if (!respuesta || !respuesta.ok) {
            throw new Error("Funcionario no encontrado");
        }

        const funcionario = await respuesta.json();
        modoEdicion = true;

        document.getElementById("funcionarioId").value = funcionario.id;
        document.getElementById("cedula").value = funcionario.cedula || "";
        document.getElementById("cedula").readOnly = true;
        document.getElementById("cedulaError").style.display = "none";

        document.getElementById("nombres").value = funcionario.nombres || funcionario.nombrePila || "";
        document.getElementById("apellidos").value = funcionario.apellidos || "";
        seleccionarValorOAgregar("unidadAdministrativa", funcionario.unidadAdministrativa || "");
        seleccionarValorOAgregar("cargo", funcionario.cargo || "");
        document.getElementById("estado").value = funcionario.estado || "ACTIVO";
        document.getElementById("codigoBiometrico").value = funcionario.codigoBiometrico || "";

        document.getElementById("tituloFormulario").textContent = "Editar funcionario";
        const panel = document.getElementById("formularioFuncionario");
        panel.style.display = "block";
        panel.scrollIntoView({ behavior: "smooth", block: "start" });

        setTimeout(() => {
            document.getElementById("nombres")?.focus();
        }, 250);
    } catch (error) {
        console.error(error);
        alert("Error al obtener el funcionario");
    }
}

// ========================================
// ELIMINAR / DESACTIVAR
// ========================================
async function eliminarFuncionario(id) {
    if (!confirm("¿Está seguro de desactivar este funcionario? Sus computadoras asignadas quedarán disponibles.")) {
        return;
    }

    try {
        const respuesta = await apiFetch(`/api/funcionarios/${id}`, {
            method: "DELETE"
        });

        if (!respuesta) return;

        if (!respuesta.ok) {
            const mensaje = await obtenerMensajeError(respuesta, "No se pudo desactivar el funcionario");
            alert(mensaje);
            return;
        }

        alert("Funcionario desactivado correctamente.");
        await cargarFuncionarios();
    } catch (error) {
        console.error(error);
        alert("Error de conexión con el servidor");
    }
}

// ========================================
// INICIALIZACIÓN
// ========================================
async function iniciarPagina() {
    await cargarCatalogosFormulario();
    await cargarFuncionarios();

    const idEdicion = new URLSearchParams(window.location.search).get("editar");
    if (/^\d+$/.test(idEdicion || "")) {
        window.history.replaceState({}, document.title, window.location.pathname);
        await editarFuncionario(Number(idEdicion));
    }
}

iniciarPagina();
