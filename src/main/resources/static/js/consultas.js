const token = localStorage.getItem("token");

// ========================================
// VERIFICAR SESIÓN
// ========================================

if (!token) {
    window.location.href = "/login";
}

// ========================================
// CERRAR SESIÓN
// ========================================

const logout = document.getElementById("logout");
if (logout) {
    logout.addEventListener("click", event => {
        event.preventDefault();
        localStorage.removeItem("token");
        localStorage.removeItem("rol");
        localStorage.removeItem("usuario");
        localStorage.removeItem("nombre");
        localStorage.removeItem("idUsuario");
        window.location.href = "/login";
    });
}

// ========================================
// PESTAÑAS
// ========================================

document.querySelectorAll(".consulta-tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".consulta-tab").forEach(t => t.classList.remove("active"));
        document.querySelectorAll(".consulta-panel").forEach(panel => panel.classList.remove("active"));

        tab.classList.add("active");
        document.getElementById(`panel-${tab.dataset.tab}`).classList.add("active");
        ocultarMensaje();
    });
});

// ========================================
// VALIDACIÓN CÉDULA ECUATORIANA
// ========================================

function esCedulaValida(cedula) {
    if (!cedula || cedula.length !== 10 || !/^\d{10}$/.test(cedula)) return false;
    const prov = parseInt(cedula.substring(0, 2), 10);
    if (!((prov >= 1 && prov <= 24) || prov === 30)) return false;
    const tercerDigito = parseInt(cedula.charAt(2), 10);
    if (tercerDigito < 0 || tercerDigito > 6) return false;

    const coeficientes = [2, 1, 2, 1, 2, 1, 2, 1, 2];
    let suma = 0;
    for (let i = 0; i < 9; i++) {
        let val = parseInt(cedula.charAt(i), 10) * coeficientes[i];
        if (val >= 10) val -= 9;
        suma += val;
    }
    const digitoVerificador = parseInt(cedula.charAt(9), 10);
    const decenaSuperior = Math.ceil(suma / 10.0) * 10;
    let resultado = decenaSuperior - suma;
    if (resultado === 10) resultado = 0;
    return resultado === digitoVerificador;
}

// ========================================
// CONSULTA FUNCIONARIO
// ========================================

document.getElementById("formFuncionario")?.addEventListener("submit", async event => {
    event.preventDefault();

    const cedula = document.getElementById("cedula").value.trim();

    if (!cedula || !/^\d{10}$/.test(cedula)) {
        mostrarMensaje("La cédula debe contener exactamente 10 dígitos numéricos.", true);
        return;
    }

    try {
        mostrarMensaje("Consultando información del funcionario...");
        const respuesta = await apiFetch(`/api/consultas/funcionario/${encodeURIComponent(cedula)}`);

        if (!respuesta) return;

        if (!respuesta.ok) {
            await mostrarErrorRespuesta(respuesta, "No se encontró el funcionario.");
            ocultarResultado("resultadoFuncionario");
            return;
        }

        const datos = await respuesta.json();

        mostrarFuncionario(datos.funcionario);
        mostrarComputadoras(datos.computadoras);
        mostrarMantenimientosFuncionario(datos.mantenimientos);
        mostrarEquiposTecnologicos(datos.equiposTecnologicos);

        document.getElementById("resultadoFuncionario").style.display = "block";
        guardarConsultaReciente(datos.funcionario);
        ocultarMensaje();

    } catch (error) {
        console.error(error);
        ocultarResultado("resultadoFuncionario");
        mostrarMensaje("Error de conexión con el servidor.", true);
    }
});

// ========================================
// MOSTRAR FUNCIONARIO
// ========================================

function mostrarFuncionario(funcionario) {
    const nombresCompletos = funcionario.nombres && funcionario.apellidos
        ? `${funcionario.nombres} ${funcionario.apellidos}`
        : (funcionario.nombrePila || "N/A");

    document.getElementById("informacionFuncionario").innerHTML = `
        <div class="form-group">
            <label>Cédula</label>
            <span style="font-weight: 700; color: #1e293b; font-size: 14px;">${esc(funcionario.cedula)}</span>
        </div>
        <div class="form-group">
            <label>Nombres y Apellidos</label>
            <span style="font-weight: 700; color: #2563eb; font-size: 14px;">${esc(nombresCompletos)}</span>
        </div>
        <div class="form-group">
            <label>Unidad Administrativa</label>
            <span>${esc(funcionario.unidadAdministrativa)}</span>
        </div>
        <div class="form-group">
            <label>Cargo</label>
            <span>${esc(funcionario.cargo)}</span>
        </div>
        <div class="form-group">
            <label>Estado</label>
            <div>
                <span class="badge ${funcionario.estado === 'Activo' || funcionario.estado === 'ACTIVO' ? 'badge-success' : 'badge-danger'}">
                    ${esc(funcionario.estado)}
                </span>
            </div>
        </div>
        <div class="form-group">
            <label>Código Biométrico</label>
            <span>${esc(funcionario.codigoBiometrico)}</span>
        </div>
        <div class="form-group" style="grid-column: span 2; display: flex; align-items: center; gap: 8px; margin-top: 10px;">
            <a href="/funcionarios?editar=${encodeURIComponent(funcionario.id)}" class="btn btn-warning btn-sm">
                ✏️ Editar Funcionario
            </a>
        </div>
    `;
}

// ========================================
// COMPUTADORAS DEL FUNCIONARIO (1..N)
// ========================================

function mostrarComputadoras(computadoras) {
    const tabla = document.getElementById("tablaComputadoras");
    tabla.innerHTML = "";

    if (!computadoras || computadoras.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="11" class="sin-datos">
                    Este funcionario no tiene computadoras asignadas actualmente.
                </td>
            </tr>
        `;
        return;
    }

    computadoras.forEach(pc => {
        tabla.innerHTML += `
            <tr>
                <td><strong>${pc.id}</strong></td>
                <td><span style="font-weight: 700; color: #1e293b;">${esc(pc.serie)}</span></td>
                <td>${esc(pc.nombreEquipo)}</td>
                <td>${esc(pc.tipo)} / ${esc(pc.marca)}</td>
                <td>${esc(pc.modelo)}</td>
                <td>${esc(pc.tipoProcesador)} ${esc(pc.generacionProcesador || "")}</td>
                <td>${esc(pc.memoriaRAM)} / ${esc(pc.tipoDisco)} ${esc(pc.capacidadDiscoGB)}GB</td>
                <td>${esc(pc.sistemaOperativo)}</td>
                <td>${esc(pc.ubicacion)}</td>
                <td><span class="badge badge-info">${esc(pc.estado || "N/A")}</span></td>
                <td class="text-center">
                    <div class="acciones-iconos" style="justify-content: center;">
                        <a href="/computadoras?editar=${encodeURIComponent(pc.id)}" class="btn-icon btn-icon-warning" title="Editar Computadora">
                            ✏️
                        </a>
                    </div>
                </td>
            </tr>
        `;
    });
}

// ========================================
// MANTENIMIENTOS DEL FUNCIONARIO
// ========================================

function mostrarMantenimientosFuncionario(mantenimientos) {
    const tabla = document.getElementById("tablaMantenimientosFuncionario");
    tabla.innerHTML = "";

    if (!mantenimientos || mantenimientos.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="9" class="sin-datos">
                    No existen mantenimientos registrados para sus computadoras.
                </td>
            </tr>
        `;
        return;
    }

    mantenimientos.forEach(m => {
        tabla.innerHTML += `
            <tr>
                <td>${formatearFecha(m.fechaMantenimiento)}</td>
                <td>
                    <strong>${esc(m.nombreEquipo)}</strong>
                    <br><small style="color: #64748b;">Serie: ${esc(m.serieComputadora)}</small>
                </td>
                <td>${esc(m.tipoMantenimiento)}</td>
                <td>${esc(m.diagnostico)}</td>
                <td>${esc(m.trabajoRealizado)}</td>
                <td>${esc(m.nombreResponsable || m.nombreUsuario || "N/A")}</td>
                <td><strong>$${esc(m.costo || 0)}</strong></td>
                <td><span class="badge badge-info">${esc(m.estadoPosterior || "N/A")}</span></td>
                <td class="text-center">
                    <div class="acciones-iconos" style="justify-content: center;">
                        <button type="button" class="btn-icon btn-icon-info" title="Ver detalle del mantenimiento" aria-label="Ver detalle del mantenimiento" onclick="verDetalleMantenimiento(${jsonAttr(m)})">
                            👁️
                        </button>
                        <a href="/mantenimientos?editar=${encodeURIComponent(m.id)}" class="btn-icon btn-icon-warning" title="Editar Mantenimiento">
                            ✏️
                        </a>
                    </div>
                </td>
            </tr>
        `;
    });
}

// ========================================
// EQUIPAMIENTO TECNOLÓGICO DEL FUNCIONARIO
// ========================================

function mostrarEquiposTecnologicos(equipos) {
    const tabla = document.getElementById("tablaEquiposFuncionario");
    tabla.innerHTML = "";

    if (!equipos || equipos.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="11" class="sin-datos">
                    Este funcionario no tiene periféricos registrados.
                </td>
            </tr>
        `;
        return;
    }

    equipos.forEach(eq => {
        tabla.innerHTML += `
            <tr>
                <td><strong>${eq.id}</strong></td>
                <td>${esc(eq.tipoEquipo)}</td>
                <td>${esc(eq.marca)}</td>
                <td>${esc(eq.modelo || "N/A")}</td>
                <td><span style="font-weight: 700; color: #1e293b;">${esc(eq.serie || "N/A")}</span></td>
                <td>${esc(eq.detalle || "N/A")}</td>
                <td><span class="badge badge-info">${esc(eq.estado || "N/A")}</span></td>
                <td>${esc(eq.etiquetaConstatacion || "—")}</td>
                <td>${esc(eq.nroPR || "—")}</td>
                <td>${esc(eq.ipTelefono || "—")}</td>
                <td class="text-center">
                    <div class="acciones-iconos" style="justify-content: center;">
                        <a href="/equipos-tecnologicos?editar=${encodeURIComponent(eq.id)}" class="btn-icon btn-icon-warning" title="Editar Equipo Tecnológico">
                            ✏️
                        </a>
                    </div>
                </td>
            </tr>
        `;
    });
}

// ========================================
// CONSULTA COMPUTADORA
// ========================================

document.getElementById("formComputadora")?.addEventListener("submit", async event => {
    event.preventDefault();

    const serie = document.getElementById("serieComputadora").value.trim();
    const cedula = document.getElementById("cedulaComputadora")?.value.trim() || "";
    const nombre = document.getElementById("nombreEquipo").value.trim();

    if (!serie && !cedula && !nombre) {
        mostrarMensaje("Ingrese al menos un criterio de búsqueda (Serie, Cédula o Nombre).", true);
        return;
    }

    const params = new URLSearchParams();
    if (serie) params.append("serie", serie);
    if (cedula) params.append("cedula", cedula);
    if (nombre) params.append("nombre", nombre);

    try {
        mostrarMensaje("Consultando computadora e historial...");
        const respuesta = await apiFetch(`/api/consultas/computadoras?${params.toString()}`);

        if (!respuesta) return;

        if (!respuesta.ok) {
            await mostrarErrorRespuesta(respuesta, "No se encontraron computadoras con esos criterios.");
            ocultarResultado("resultadoComputadora");
            return;
        }

        const resultados = await respuesta.json();

        if (!resultados || resultados.length === 0) {
            ocultarResultado("resultadoComputadora");
            mostrarMensaje("No se encontraron computadoras con los criterios indicados.", true);
            return;
        }

        mostrarResultadosComputadoras(resultados);
        document.getElementById("resultadoComputadora").style.display = "block";
        ocultarMensaje();

    } catch (error) {
        console.error(error);
        ocultarResultado("resultadoComputadora");
        mostrarMensaje("Error de conexión con el servidor.", true);
    }
});

// ========================================
// MOSTRAR RESULTADOS COMPUTADORAS
// ========================================

function mostrarResultadosComputadoras(resultados) {
    const contenedor = document.getElementById("listaComputadorasConsulta");
    contenedor.innerHTML = "";

    resultados.forEach(resultado => {
        const pc = resultado.computadora;
        const actual = resultado.funcionarioActual;
        const asignaciones = resultado.historialAsignaciones || [];
        const mantenimientos = resultado.historialMantenimientos || [];

        const nombreFuncActual = actual
            ? (actual.nombres && actual.apellidos ? `${actual.nombres} ${actual.apellidos}` : actual.nombrePila)
            : null;

        const historialHtml = asignaciones.length
            ? asignaciones.map(a => `
                <tr>
                    <td><strong>${esc(a.nombreFuncionario || "Sin nombre")}</strong></td>
                    <td>${esc(a.cedulaFuncionario || "N/A")}</td>
                    <td>${formatearFecha(a.fechaAsignacion)}</td>
                    <td>${a.fechaFin ? formatearFecha(a.fechaFin) : '<span class="badge badge-success">Actual</span>'}</td>
                </tr>
            `).join("")
            : `<tr><td colspan="4" class="sin-datos">No existe historial de asignaciones previas.</td></tr>`;

        const mantenimientosHtml = mantenimientos.length
            ? mantenimientos.map(m => `
                <tr>
                    <td>${formatearFecha(m.fechaMantenimiento)}</td>
                    <td>${esc(m.tipoMantenimiento)}</td>
                    <td>${esc(m.diagnostico)}</td>
                    <td>${esc(m.trabajoRealizado)}</td>
                    <td>${esc(m.nombreResponsable || m.nombreUsuario)}</td>
                    <td><strong>$${esc(m.costo || 0)}</strong></td>
                    <td><span class="badge badge-info">${esc(m.estadoPosterior || "N/A")}</span></td>
                    <td class="text-center">
                        <div class="acciones-iconos" style="justify-content: center;">
                            <button type="button" class="btn-icon btn-icon-info" title="Ver detalle del mantenimiento" aria-label="Ver detalle del mantenimiento" onclick="verDetalleMantenimiento(${jsonAttr(m)})">
                                👁️
                            </button>
                            <a href="/mantenimientos?editar=${encodeURIComponent(m.id)}" class="btn-icon btn-icon-warning" title="Editar">✏️</a>
                        </div>
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="8" class="sin-datos">No existen mantenimientos registrados para este equipo.</td></tr>`;

        contenedor.innerHTML += `
            <section class="panel consulta-result-card resultado-computadora" data-computadora-id="${Number(pc.id)}">
                <div class="panel-header" style="margin-bottom: 8px;">
                    <div>
                        <h2>${esc(pc.nombreEquipo || "Computadora")} — Serie: <span style="color: #2563eb;">${esc(pc.serie)}</span></h2>
                        <p class="consulta-subtitle">ID: ${pc.id} | Tipo: ${esc(pc.tipo)} | Marca: ${esc(pc.marca)} | Modelo: ${esc(pc.modelo)}</p>
                    </div>
                    <div class="form-buttons" style="margin-top: 0;">
                        <a href="/computadoras?editar=${encodeURIComponent(pc.id)}" class="btn btn-warning btn-sm">✏️ Editar Equipo</a>
                        <button type="button" class="btn btn-secondary btn-sm" onclick="generarReporteComputadora(${Number(pc.id)})">🖨 Imprimir Ficha</button>
                    </div>
                </div>

                <!-- ESPECIFICACIONES -->
                <div class="consulta-section">
                    <h3>📋 1. Datos Generales</h3>
                    <div class="form-grid-cuatro">
                        <div class="form-group"><label>Nombre de equipo</label><span><strong>${esc(pc.nombreEquipo)}</strong></span></div>
                        <div class="form-group"><label>Serie</label><span><strong>${esc(pc.serie)}</strong></span></div>
                        <div class="form-group"><label>Tipo de equipo</label><span>${esc(pc.tipo)}</span></div>
                        <div class="form-group"><label>Marca</label><span>${esc(pc.marca)}</span></div>
                        <div class="form-group"><label>Modelo</label><span>${esc(pc.modelo)}</span></div>
                        <div class="form-group"><label>Procedencia</label><span>${esc(pc.procedencia || "Institucional")}</span></div>
                        <div class="form-group"><label>Ubicación</label><span>${esc(pc.ubicacion)}</span></div>
                        <div class="form-group"><label>Estado</label><span><span class="badge badge-info">${esc(pc.estado || "N/A")}</span></span></div>
                        <div class="form-group"><label>Funcionario asignado</label><span>${nombreFuncActual ? esc(nombreFuncActual) : 'Sin asignar'}</span></div>
                        <div class="form-group"><label>Cédula del funcionario</label><span>${actual ? esc(actual.cedula) : "—"}</span></div>
                        <div class="form-group"><label>Cargo</label><span>${actual ? esc(actual.cargo || "N/A") : "—"}</span></div>
                        <div class="form-group"><label>Unidad administrativa</label><span>${actual ? esc(actual.unidadAdministrativa || "N/A") : "—"}</span></div>
                    </div>
                </div>

                <div class="consulta-section">
                    <h3>💿 2. Especificación de Software</h3>
                    <div class="form-grid-cuatro">
                        <div class="form-group"><label>Sistema Operativo</label><span>${esc(pc.sistemaOperativo)}</span></div>
                        <div class="form-group"><label>Ofimática</label><span>${esc(pc.office || "N/A")}</span></div>
                        <div class="form-group"><label>Antivirus</label><span>${esc(pc.antivirus || "N/A")}</span></div>
                        <div class="form-group"><label>Observación (Software)</label><span>${esc(pc.observacionSoftware || "Sin observaciones")}</span></div>
                    </div>
                </div>

                <div class="consulta-section">
                    <h3>🌐 3. Red de Datos</h3>
                    <div class="form-grid-cuatro">
                        <div class="form-group"><label>Dirección IP</label><span>${esc(pc.ip || "N/A")}</span></div>
                        <div class="form-group"><label>MAC LAN</label><span>${esc(pc.macLan || "N/A")}</span></div>
                        <div class="form-group"><label>MAC WIFI</label><span>${esc(pc.macWifi || "N/A")}</span></div>
                        <div class="form-group"><label>Nro. P.R.</label><span>${esc(pc.nroPR || "N/A")}</span></div>
                        <div class="form-group" style="grid-column: 1 / -1;"><label>Observación (Red de datos)</label><span>${esc(pc.observacionRed || "Sin observaciones")}</span></div>
                    </div>
                </div>

                <div class="consulta-section">
                    <h3>💻 4. Especificación de Hardware</h3>
                    <div class="form-grid-cuatro">
                        <div class="form-group"><label>Tipo de procesador</label><span>${esc(pc.tipoProcesador)}</span></div>
                        <div class="form-group"><label>Generación de procesador</label><span>${esc(pc.generacionProcesador || "N/A")}</span></div>
                        <div class="form-group"><label>Velocidad del procesador</label><span>${esc(pc.velocidadProcesador || "N/A")}</span></div>
                        <div class="form-group"><label>Memoria RAM</label><span>${esc(pc.memoriaRAM)}</span></div>
                        <div class="form-group"><label>Tipo de disco</label><span>${esc(pc.tipoDisco)}</span></div>
                        <div class="form-group"><label>Capacidad de disco</label><span>${esc(pc.capacidadDiscoGB)} GB</span></div>
                    </div>
                </div>

                <!-- FUNCIONARIO ACTUAL -->
                <div class="consulta-section">
                    <h3>Funcionario Asignado Actual</h3>
                    <div class="estado-actual ${actual ? '' : 'vacio'}">
                        ${actual ? `
                            <strong>👤 ${esc(nombreFuncActual)}</strong><br>
                            Cédula: <strong>${esc(actual.cedula)}</strong> | Cargo: ${esc(actual.cargo || "N/A")} | Unidad: ${esc(actual.unidadAdministrativa || "N/A")}
                        ` : `⚠️ Este equipo no tiene ningún funcionario asignado actualmente.`}
                    </div>
                </div>

                <!-- HISTORIAL ASIGNACIONES -->
                <div class="consulta-section">
                    <h3>Historial de Asignaciones</h3>
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Funcionario</th>
                                    <th>Cédula</th>
                                    <th>Fecha Asignación</th>
                                    <th>Fecha Desasignación</th>
                                </tr>
                            </thead>
                            <tbody>${historialHtml}</tbody>
                        </table>
                    </div>
                </div>

                <!-- HISTORIAL MANTENIMIENTOS -->
                <div class="consulta-section">
                    <h3>Historial de Mantenimientos</h3>
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Fecha</th>
                                    <th>Tipo</th>
                                    <th>Diagnóstico</th>
                                    <th>Trabajo Realizado</th>
                                    <th>Responsable</th>
                                    <th>Costo</th>
                                    <th>Estado Post.</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>${mantenimientosHtml}</tbody>
                        </table>
                    </div>
                </div>
            </section>
        `;
    });
}

// ========================================
// CONSULTA MANTENIMIENTO
// ========================================

document.getElementById("formMantenimiento")?.addEventListener("submit", async event => {
    event.preventDefault();

    const serie = document.getElementById("mantenimientoSerie")?.value.trim() || "";
    const cedula = document.getElementById("mantenimientoCedula")?.value.trim() || "";
    const desde = document.getElementById("desde").value;
    const hasta = document.getElementById("hasta").value;

    const params = new URLSearchParams();
    if (serie) params.append("serie", serie);
    if (cedula) params.append("cedula", cedula);
    if (desde) params.append("desde", desde);
    if (hasta) params.append("hasta", hasta);

    if (desde && hasta && desde > hasta) {
        mostrarMensaje("La fecha inicial no puede ser posterior a la fecha final.", true);
        return;
    }

    try {
        mostrarMensaje("Consultando mantenimientos...");
        const url = params.toString() ? `/api/mantenimientos?${params.toString()}` : "/api/mantenimientos";
        const respuesta = await apiFetch(url);

        if (!respuesta) return;

        if (!respuesta.ok) {
            await mostrarErrorRespuesta(respuesta, "No fue posible consultar los mantenimientos.");
            ocultarResultado("resultadoMantenimiento");
            return;
        }

        const datos = await respuesta.json();
        mostrarMantenimientosConsulta(datos);
        document.getElementById("resultadoMantenimiento").style.display = "block";
        ocultarMensaje();

    } catch (error) {
        console.error(error);
        ocultarResultado("resultadoMantenimiento");
        mostrarMensaje("Error de conexión con el servidor.", true);
    }
});

// ========================================
// MOSTRAR MANTENIMIENTOS TABLA
// ========================================

function mostrarMantenimientosConsulta(mantenimientos) {
    const tabla = document.getElementById("tablaMantenimientosConsulta");
    tabla.innerHTML = "";

    if (!mantenimientos || mantenimientos.length === 0) {
        tabla.innerHTML = `<tr><td colspan="10" class="sin-datos">No se encontraron mantenimientos con los criterios ingresados.</td></tr>`;
        return;
    }

    mantenimientos.forEach(m => {
        tabla.innerHTML += `
            <tr>
                <td>${formatearFecha(m.fechaMantenimiento)}</td>
                <td>
                    <strong>${esc(m.nombreEquipo)}</strong>
                    <br><small style="color: #64748b;">Serie: ${esc(m.serieComputadora)}</small>
                </td>
                <td>${esc(m.nombreFuncionario || "N/A")}</td>
                <td>${esc(m.tipoMantenimiento)}</td>
                <td>${esc(m.diagnostico)}</td>
                <td>${esc(m.trabajoRealizado)}</td>
                <td>${esc(m.nombreResponsable || m.nombreUsuario || "N/A")}</td>
                <td><strong>$${esc(m.costo || 0)}</strong></td>
                <td><span class="badge badge-info">${esc(m.estadoPosterior || "N/A")}</span></td>
                <td class="text-center">
                    <div class="acciones-iconos" style="justify-content: center;">
                        <button type="button" class="btn-icon btn-icon-info" title="Ver detalle del mantenimiento" aria-label="Ver detalle del mantenimiento" onclick="verDetalleMantenimiento(${jsonAttr(m)})">
                            👁️
                        </button>
                        <a href="/mantenimientos?editar=${encodeURIComponent(m.id)}" class="btn-icon btn-icon-warning" title="Editar Mantenimiento">
                            ✏️
                        </a>
                    </div>
                </td>
            </tr>
        `;
    });
}

// ========================================
// CONSULTA EQUIPAMIENTO TECNOLÓGICO
// ========================================

document.getElementById("formEquipamiento")?.addEventListener("submit", async event => {
    event.preventDefault();

    const serie = document.getElementById("serieEquipo").value.trim();
    const cedula = document.getElementById("cedulaEquipo").value.trim();

    if (!serie && !cedula) {
        mostrarMensaje("Ingrese al menos un criterio de búsqueda (Serie o Cédula).", true);
        return;
    }

    const params = new URLSearchParams();
    if (serie) params.append("serie", serie);
    if (cedula) params.append("cedula", cedula);

    try {
        mostrarMensaje("Consultando periféricos...");
        const respuesta = await apiFetch(`/api/consultas/equipos-tecnologicos?${params.toString()}`);

        if (!respuesta) return;

        if (!respuesta.ok) {
            await mostrarErrorRespuesta(respuesta, "No se encontraron periféricos con esos criterios.");
            ocultarResultado("resultadoEquipamiento");
            return;
        }

        const datos = await respuesta.json();

        if (!datos || datos.length === 0) {
            ocultarResultado("resultadoEquipamiento");
            mostrarMensaje("No se encontraron periféricos con los criterios indicados.", true);
            return;
        }

        mostrarEquiposConsulta(datos);
        document.getElementById("resultadoEquipamiento").style.display = "block";
        ocultarMensaje();

    } catch (error) {
        console.error(error);
        ocultarResultado("resultadoEquipamiento");
        mostrarMensaje("Error de conexión con el servidor.", true);
    }
});

function mostrarEquiposConsulta(equipos) {
    const tabla = document.getElementById("tablaEquiposConsulta");
    tabla.innerHTML = "";

    if (!equipos || equipos.length === 0) {
        tabla.innerHTML = `<tr><td colspan="13" class="sin-datos">No se encontraron periféricos con los criterios ingresados.</td></tr>`;
        return;
    }

    equipos.forEach(eq => {
        tabla.innerHTML += `
            <tr>
                <td><strong>${eq.id}</strong></td>
                <td>${esc(eq.nombreFuncionario || "Sin asignar")}</td>
                <td>${esc(eq.unidadAdministrativaFuncionario || "—")}</td>
                <td>${esc(eq.tipoEquipo)}</td>
                <td>${esc(eq.marca)}</td>
                <td>${esc(eq.modelo || "N/A")}</td>
                <td><span style="font-weight: 700; color: #1e293b;">${esc(eq.serie || "N/A")}</span></td>
                <td>${esc(eq.detalle || "N/A")}</td>
                <td><span class="badge badge-info">${esc(eq.estado || "N/A")}</span></td>
                <td>${esc(eq.etiquetaConstatacion || "—")}</td>
                <td>${esc(eq.nroPR || "—")}</td>
                <td>${esc(eq.ipTelefono || "—")}</td>
                <td class="text-center">
                    <div class="acciones-iconos" style="justify-content: center;">
                        <a href="/equipos-tecnologicos?editar=${encodeURIComponent(eq.id)}" class="btn-icon btn-icon-warning" title="Editar Equipo Tecnológico">
                            ✏️
                        </a>
                    </div>
                </td>
            </tr>
        `;
    });
}

// ========================================
// LIMPIAR BOTONES
// ========================================

document.getElementById("limpiarFuncionario")?.addEventListener("click", () => {
    document.getElementById("formFuncionario").reset();
    ocultarResultado("resultadoFuncionario");
    ocultarMensaje();
});

document.getElementById("limpiarComputadora")?.addEventListener("click", () => {
    document.getElementById("formComputadora").reset();
    ocultarResultado("resultadoComputadora");
    document.getElementById("listaComputadorasConsulta").innerHTML = "";
    ocultarMensaje();
});

document.getElementById("limpiarMantenimiento")?.addEventListener("click", () => {
    document.getElementById("formMantenimiento").reset();
    ocultarResultado("resultadoMantenimiento");
    document.getElementById("tablaMantenimientosConsulta").innerHTML = "";
    ocultarMensaje();
});

document.getElementById("limpiarEquipamiento")?.addEventListener("click", () => {
    document.getElementById("formEquipamiento").reset();
    ocultarResultado("resultadoEquipamiento");
    document.getElementById("tablaEquiposConsulta").innerHTML = "";
    ocultarMensaje();
});

// ========================================
// UTILIDADES
// ========================================

function formatearFecha(fecha) {
    if (!fecha) return "N/A";
    const fechaObj = new Date(fecha);
    return isNaN(fechaObj.getTime()) ? String(fecha) : fechaObj.toLocaleDateString("es-EC", { dateStyle: "short" });
}

function esc(valor) {
    if (valor === null || valor === undefined || valor === "") return "N/A";
    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function jsonAttr(obj) {
    return JSON.stringify(obj)
        .replaceAll("&", "&amp;")
        .replaceAll('"', "&quot;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
}

function verDetalleMantenimiento(m) {
    const contenedor = document.getElementById("contenidoDetalleMantenimiento");
    if (!contenedor) return;

    const fmt = v => (v === null || v === undefined || v === "") ? "N/A" : esc(v);
    const formaFecha = v => v ? escaDate(v) : "N/A";
    const costo = (m.costo === null || m.costo === undefined || m.costo === "")
        ? "N/A"
        : `$${Number(m.costo).toFixed(2)}`;

    contenedor.innerHTML = `
        <div class="consulta-section">
            <div class="two-col">
                <div class="form-group"><label>ID</label><span><strong>#${fmt(m.id)}</strong></span></div>
                <div class="form-group"><label>Tipo de mantenimiento</label><span><strong>${fmt(m.tipoMantenimiento)}</strong></span></div>
                <div class="form-group"><label>Estado del mantenimiento</label><span><span class="badge badge-info">${fmt(m.estadoMantenimiento)}</span></span></div>
                <div class="form-group"><label>Costo</label><span><strong>${costo}</strong></span></div>
                <div class="form-group"><label>Responsable</label><span>${fmt(m.nombreResponsable)}</span></div>
                <div class="form-group"><label>Estado posterior de la computadora</label><span>${fmt(m.estadoPosterior)}</span></div>
                <div class="form-group"><label>Observaciones</label><span>${fmt(m.observaciones || "Sin observaciones")}</span></div>
            </div>
        </div>

        <div class="consulta-section">
            <h3>🖥 Computadora</h3>
            <div class="two-col">
                <div class="form-group"><label>ID computadora</label><span>${fmt(m.computadoraId)}</span></div>
                <div class="form-group"><label>Nombre de equipo</label><span><strong>${fmt(m.nombreEquipo)}</strong></span></div>
                <div class="form-group"><label>Serie</label><span>${fmt(m.serieComputadora)}</span></div>
                <div class="form-group"><label>Tipo de equipo</label><span>${fmt(m.tipoEquipo)}</span></div>
                <div class="form-group"><label>Marca</label><span>${fmt(m.marcaEquipo)}</span></div>
                <div class="form-group"><label>Modelo</label><span>${fmt(m.modeloEquipo)}</span></div>
                <div class="form-group"><label>Ubicación</label><span>${fmt(m.ubicacionEquipo)}</span></div>
                <div class="form-group"><label>Estado actual</label><span><span class="badge badge-info">${fmt(m.estadoActualComputadora)}</span></span></div>
            </div>
        </div>

        <div class="consulta-section">
            <h3>👤 Funcionario</h3>
            <div class="two-col">
                <div class="form-group"><label>ID funcionario</label><span>${fmt(m.funcionarioId)}</span></div>
                <div class="form-group"><label>Cédula</label><span>${fmt(m.cedulaFuncionario)}</span></div>
                <div class="form-group"><label>Nombre</label><span><strong>${fmt(m.nombreFuncionario)}</strong></span></div>
            </div>
        </div>

        <div class="consulta-section">
            <h3>🗓 Fechas y Registro</h3>
            <div class="two-col">
                <div class="form-group"><label>Fecha/hora (generación)</label><span>${formaFecha(m.fechaHora)}</span></div>
                <div class="form-group"><label>Fecha de mantenimiento</label><span>${formaFecha(m.fechaMantenimiento)}</span></div>
                <div class="form-group"><label>Fecha de creación del registro</label><span>${formaFecha(m.fechaCreacion)}</span></div>
                <div class="form-group"><label>Registrado por (usuario)</label><span>${fmt(m.nombreUsuario)}</span></div>
            </div>
        </div>

        <div class="consulta-section">
            <h3>📝 Descripción del mantenimiento</h3>
            <div class="two-col">
                <div class="form-group" style="grid-column: 1 / -1;"><label>Diagnóstico</label><span>${fmt(m.diagnostico)}</span></div>
                <div class="form-group" style="grid-column: 1 / -1;"><label>Trabajo realizado</label><span>${fmt(m.trabajoRealizado)}</span></div>
            </div>
        </div>
    `;

    document.getElementById("modalDetalleMantenimiento").style.display = "flex";
}

function escaDate(v) {
    return esc(String(v).replace("T", " ").substring(0, 16));
}

function cerrarDetalleMantenimiento() {
    const modal = document.getElementById("modalDetalleMantenimiento");
    if (modal) modal.style.display = "none";
}

async function mostrarErrorRespuesta(respuesta, mensajePorDefecto) {
    mostrarMensaje(await obtenerMensajeError(respuesta, mensajePorDefecto), true);
}

function mostrarMensaje(mensaje, error = false) {
    const elemento = document.getElementById("mensajeConsulta");
    if (!elemento) return;
    elemento.textContent = mensaje;
    elemento.style.display = "block";
    elemento.style.borderLeft = error ? "5px solid #dc2626" : "5px solid #2563eb";
}

function ocultarMensaje() {
    const elemento = document.getElementById("mensajeConsulta");
    if (elemento) elemento.style.display = "none";
}

function ocultarResultado(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = "none";
}

// ========================================
// REPORTES
// ========================================

function generarReporte(titulo, selector) {
    const resultado = document.querySelector(selector);
    if (!resultado) return;

    const contenido = resultado.cloneNode(true);
    contenido.querySelectorAll("button, .acciones-iconos, .form-buttons").forEach(el => el.remove());

    const ventana = window.open("", "_blank");
    if (!ventana) {
        mostrarMensaje("El navegador bloqueó la ventana del reporte. Permita las ventanas emergentes e intente nuevamente.", true);
        return;
    }

    ventana.document.write(`
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>${esc(titulo)} - Sistema TIC</title>
            <link rel="stylesheet" href="/css/estilos.css">
            <style>
                body { padding: 30px; background: #fff; }
                .reporte-encabezado { margin-bottom: 20px; border-bottom: 2px solid #2563eb; padding-bottom: 10px; }
                .sidebar, .topbar, .form-buttons, .consulta-tabs { display: none !important; }
                .main-content { margin: 0; padding: 0; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                th, td { border: 1px solid #cbd5e1; padding: 6px 10px; font-size: 12px; }
                th { background: #f1f5f9; }
                @media print { body { padding: 0; } }
            </style>
        </head>
        <body>
            <header class="reporte-encabezado">
                <h2>SISTEMA TIC - GOBIERNO DEL ECUADOR</h2>
                <h3>${esc(titulo)}</h3>
                <p>Fecha de emisión: ${esc(new Date().toLocaleString("es-EC"))}</p>
            </header>
            ${contenido.innerHTML}
            <script>window.addEventListener("load", () => window.print());</script>
        </body>
        </html>
    `);
    ventana.document.close();
}

function generarReporteComputadora(id) {
    generarReporte("Reporte Ficha Técnica de Computadora", `.resultado-computadora[data-computadora-id="${Number(id)}"]`);
}

document.getElementById("generarReporteFuncionario")?.addEventListener("click", () => {
    generarReporte("Ficha de Funcionario y Equipos Asignados", "#resultadoFuncionario");
});

document.getElementById("generarReporteMantenimiento")?.addEventListener("click", () => {
    generarReporte("Reporte General de Mantenimientos", "#resultadoMantenimiento");
});

document.getElementById("generarReporteEquipamiento")?.addEventListener("click", () => {
    generarReporte("Reporte de Periféricos", "#resultadoEquipamiento");
});

function guardarConsultaReciente(funcionario) {
    const consultas = JSON.parse(localStorage.getItem("ultimasConsultas") || "[]");
    const nom = funcionario.nombres && funcionario.apellidos ? `${funcionario.nombres} ${funcionario.apellidos}` : funcionario.nombrePila;
    consultas.unshift({
        nombre: nom || "Funcionario",
        tipo: `Consulta Funcionario - ${funcionario.cedula}`,
        icono: "♙",
        hora: new Date().toLocaleTimeString("es-EC", { hour: "2-digit", minute: "2-digit" })
    });
    localStorage.setItem("ultimasConsultas", JSON.stringify(consultas.slice(0, 10)));
}

const rolAdmin = localStorage.getItem("rol");
const menuAdministracion = document.getElementById("menuAdministracion");
if (menuAdministracion && rolAdmin !== "ADMIN") {
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
