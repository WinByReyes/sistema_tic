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

const logout =
    document.getElementById("logout");


if (logout) {

    logout.addEventListener(
        "click",
        event => {

            event.preventDefault();

            localStorage.removeItem("token");
            localStorage.removeItem("rol");
            localStorage.removeItem("usuario");
            localStorage.removeItem("nombre");

            window.location.href =
                "/login";

        }
    );

}


// ========================================
// PESTAÑAS
// ========================================

document
    .querySelectorAll(".consulta-tab")
    .forEach(tab => {

        tab.addEventListener(
            "click",
            () => {

                document
                    .querySelectorAll(".consulta-tab")
                    .forEach(t =>
                        t.classList.remove("active")
                    );


                document
                    .querySelectorAll(".consulta-panel")
                    .forEach(panel =>
                        panel.classList.remove("active")
                    );


                tab.classList.add("active");


                document
                    .getElementById(
                        `panel-${tab.dataset.tab}`
                    )
                    .classList.add("active");


                ocultarMensaje();

            }
        );

    });


// ========================================
// CONSULTA FUNCIONARIO
// ========================================

document
    .getElementById("formFuncionario")
    ?.addEventListener(
        "submit",
        async event => {

            event.preventDefault();


            const cedula =
                document
                    .getElementById("cedula")
                    .value
                    .trim();


            if (!/^\d{10}$/.test(cedula)) {

                mostrarMensaje(
                    "La cédula debe contener exactamente 10 números.",
                    true
                );

                return;

            }


            try {

                mostrarMensaje(
                    "Consultando información..."
                );


                const respuesta =
                    await apiFetch(
                        `/api/consultas/funcionario/${encodeURIComponent(cedula)}`
                    );


                if (!respuesta) {

                    return;

                }


                if (!respuesta.ok) {

                    await mostrarErrorRespuesta(
                        respuesta,
                        "No se encontró el funcionario."
                    );


                    ocultarResultado(
                        "resultadoFuncionario"
                    );


                    return;

                }


                const datos =
                    await respuesta.json();


                mostrarFuncionario(
                    datos.funcionario
                );


                mostrarComputadoras(
                    datos.computadoras
                );


                mostrarMantenimientosFuncionario(
                    datos.mantenimientos
                );


                document
                    .getElementById(
                        "resultadoFuncionario"
                    )
                    .style.display =
                    "block";


                guardarConsultaReciente(
                    datos.funcionario
                );


                ocultarMensaje();

            } catch (error) {

                console.error(error);


                ocultarResultado(
                    "resultadoFuncionario"
                );


                mostrarMensaje(
                    "Error de conexión con el servidor.",
                    true
                );

            }

        }
    );


// ========================================
// MOSTRAR FUNCIONARIO
// ========================================

function mostrarFuncionario(
    funcionario
) {

    document
        .getElementById(
            "informacionFuncionario"
        )
        .innerHTML = `

            <div class="form-buttons" style="grid-column: 1 / -1;">

                ${botonEditar("funcionario", funcionario.id)}

            </div>

            <div class="form-group">

                <label>ID</label>

                <span>
                    ${esc(funcionario.id)}
                </span>

            </div>


            <div class="form-group">

                <label>Cédula</label>

                <span>
                    ${esc(funcionario.cedula)}
                </span>

            </div>


            <div class="form-group">

                <label>Nombre</label>

                <span>
                    ${esc(funcionario.nombrePila)}
                </span>

            </div>


            <div class="form-group">

                <label>Unidad administrativa</label>

                <span>
                    ${esc(
        funcionario.unidadAdministrativa
    )}
                </span>

            </div>


            <div class="form-group">

                <label>Cargo</label>

                <span>
                    ${esc(funcionario.cargo)}
                </span>

            </div>


            <div class="form-group">

                <label>Estado</label>

                <span>
                    ${esc(funcionario.estado)}
                </span>

            </div>


            <div class="form-group">

                <label>Código biométrico</label>

                <span>
                    ${esc(
        funcionario.codigoBiometrico
    )}
                </span>

            </div>

        `;

}


// ========================================
// COMPUTADORAS DEL FUNCIONARIO
// ========================================

function mostrarComputadoras(
    computadoras
) {

    const tabla =
        document.getElementById(
            "tablaComputadoras"
        );


    tabla.innerHTML = "";


    if (
        !computadoras ||
        computadoras.length === 0
    ) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="12"
                    class="sin-datos">

                    Este funcionario
                    no tiene computadoras
                    asignadas actualmente.

                </td>

            </tr>

        `;

        return;

    }


    computadoras.forEach(
        computadora => {

            tabla.innerHTML += `

                <tr>

                    <td>
                        ${esc(computadora.id)}
                    </td>

                    <td>
                        ${esc(computadora.serie)}
                    </td>

                    <td>
                        ${esc(computadora.nombreEquipo)}
                    </td>

                    <td>
                        ${esc(computadora.marca)}
                    </td>

                    <td>
                        ${esc(computadora.modelo)}
                    </td>

                    <td>
                        ${esc(
                computadora.tipoProcesador
            )}
                        ${esc(
                computadora.generacionProcesador
            )}
                    </td>

                    <td>
                        ${esc(computadora.memoriaRAM)}
                    </td>

                    <td>
                        ${esc(computadora.tipoDisco)}
                        -
                        ${esc(
                computadora.capacidadDiscoGB
            )}
                        GB
                    </td>

                    <td>
                        ${esc(
                computadora.sistemaOperativo
            )}
                    </td>

                    <td>
                        ${esc(computadora.ubicacion)}
                    </td>

                    <td>
                        ${esc(
                computadora.estado ||
                "N/A"
            )}
                    </td>

                    <td>
                        ${botonEditar("computadora", computadora.id)}
                    </td>

                </tr>

            `;

        }
    );

}


// ========================================
// MANTENIMIENTOS DEL FUNCIONARIO
// ========================================

function mostrarMantenimientosFuncionario(
    mantenimientos
) {

    const tabla =
        document.getElementById(
            "tablaMantenimientosFuncionario"
        );


    tabla.innerHTML = "";


    if (
        !mantenimientos ||
        mantenimientos.length === 0
    ) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    class="sin-datos">

                    No existen mantenimientos
                    registrados para sus
                    computadoras.

                </td>

            </tr>

        `;

        return;

    }


    mantenimientos.forEach(
        mantenimiento => {

            tabla.innerHTML += `

                <tr>

                    <td>
                        ${formatearFecha(
                mantenimiento.fechaMantenimiento
            )}
                    </td>

                    <td>

                        ${esc(
                mantenimiento.nombreEquipo
            )}

                        <br>

                        <small>

                            Serie:
                            ${esc(
                mantenimiento.serieComputadora
            )}

                        </small>

                    </td>

                    <td>
                        ${esc(
                mantenimiento.tipoMantenimiento
            )}
                    </td>

                    <td>
                        ${esc(
                mantenimiento.diagnostico
            )}
                    </td>

                    <td>
                        ${esc(
                mantenimiento.trabajoRealizado
            )}
                    </td>

                    <td>
                        ${esc(
                mantenimiento.nombreUsuario
            )}
                    </td>

                    <td>
                        $${esc(
                mantenimiento.costo
            )}
                    </td>

                    <td>
                        ${botonEditar("mantenimiento", mantenimiento.id)}
                    </td>

                </tr>

            `;

        }
    );

}


// ========================================
// CONSULTA COMPUTADORA
// ========================================

document
    .getElementById("formComputadora")
    ?.addEventListener(
        "submit",
        async event => {

            event.preventDefault();


            const nombre =
                document
                    .getElementById("nombreEquipo")
                    .value
                    .trim();


            const serie =
                document
                    .getElementById("serieComputadora")
                    .value
                    .trim();


            if (!nombre && !serie) {

                mostrarMensaje(
                    "Ingrese el nombre del equipo, la serie o ambos.",
                    true
                );

                return;

            }


            const params =
                new URLSearchParams();


            if (nombre) {

                params.append(
                    "nombre",
                    nombre
                );

            }


            if (serie) {

                params.append(
                    "serie",
                    serie
                );

            }


            try {

                mostrarMensaje(
                    "Consultando computadora..."
                );


                const respuesta =
                    await apiFetch(
                        `/api/consultas/computadoras?${params.toString()}`
                    );


                if (!respuesta) {

                    return;

                }


                if (!respuesta.ok) {

                    await mostrarErrorRespuesta(
                        respuesta,
                        "No se encontraron computadoras con esos datos."
                    );


                    ocultarResultado(
                        "resultadoComputadora"
                    );


                    return;

                }


                const resultados =
                    await respuesta.json();


                if (!resultados.length) {

                    ocultarResultado(
                        "resultadoComputadora"
                    );


                    mostrarMensaje(
                        "No se encontró una computadora con los criterios indicados.",
                        true
                    );


                    return;

                }


                mostrarResultadosComputadoras(
                    resultados
                );


                document
                    .getElementById(
                        "resultadoComputadora"
                    )
                    .style.display =
                    "block";


                ocultarMensaje();

            } catch (error) {

                console.error(error);


                ocultarResultado(
                    "resultadoComputadora"
                );


                mostrarMensaje(
                    "Error de conexión con el servidor.",
                    true
                );

            }

        }
    );


// ========================================
// MOSTRAR COMPUTADORA + HISTORIAL
// ========================================

function mostrarResultadosComputadoras(
    resultados
) {

    const contenedor =
        document.getElementById(
            "listaComputadorasConsulta"
        );


    contenedor.innerHTML = "";


    resultados.forEach(
        resultado => {

            const computadora =
                resultado.computadora;


            const actual =
                resultado.funcionarioActual;


            const asignaciones =
                resultado.historialAsignaciones ||
                [];


            const mantenimientos =
                resultado.historialMantenimientos ||
                [];


            // ------------------------------
            // HISTORIAL ASIGNACIONES
            // ------------------------------

            const historialHtml =
                asignaciones.length

                    ? asignaciones.map(
                        asignacion => `

                            <tr>

                                <td>
                                    ${esc(
                            asignacion.nombreFuncionario ||
                            "Sin nombre"
                        )}
                                </td>

                                <td>
                                    ${esc(
                            asignacion.cedulaFuncionario ||
                            "N/A"
                        )}
                                </td>

                                <td>
                                    ${formatearFecha(
                            asignacion.fechaAsignacion
                        )}
                                </td>

                                <td>

                                    ${
                            asignacion.fechaFin

                                ? formatearFecha(
                                    asignacion.fechaFin
                                )

                                : "<strong>Actual</strong>"
                        }

                                </td>

                            </tr>

                        `
                    ).join("")

                    :

                    `

                        <tr>

                            <td
                                colspan="4"
                                class="sin-datos">

                                No existe historial
                                de asignaciones.

                            </td>

                        </tr>

                    `;


            // ------------------------------
            // HISTORIAL MANTENIMIENTOS
            // ------------------------------

            const mantenimientosHtml =
                mantenimientos.length

                    ? mantenimientos.map(
                        mantenimiento => `

                            <tr>

                                <td>
                                    ${formatearFecha(
                            mantenimiento.fechaMantenimiento
                        )}
                                </td>

                                <td>
                                    ${esc(
                            mantenimiento.tipoMantenimiento
                        )}
                                </td>

                                <td>
                                    ${esc(
                            mantenimiento.diagnostico
                        )}
                                </td>

                                <td>
                                    ${esc(
                            mantenimiento.trabajoRealizado
                        )}
                                </td>

                                <td>
                                    ${esc(
                            mantenimiento.nombreUsuario
                        )}
                                </td>

                                <td>
                                    $${esc(
                            mantenimiento.costo
                        )}
                                </td>

                                <td>
                                    ${botonEditar("mantenimiento", mantenimiento.id)}
                                </td>

                            </tr>

                        `
                    ).join("")

                    :

                    `

                        <tr>

                            <td
                                colspan="7"
                                class="sin-datos">

                                No existen mantenimientos
                                registrados.

                            </td>

                        </tr>

                    `;


            // ------------------------------
            // TARJETA
            // ------------------------------

            contenedor.innerHTML += `

                <section
                        class="
                            panel
                            consulta-result-card
                            resultado-computadora
                        "
                        data-computadora-id="${Number(computadora.id)}">


                    <h2>

                        ${esc(
                computadora.nombreEquipo
            )}

                    </h2>


                    <p class="consulta-subtitle">

                        Serie:
                        ${esc(
                computadora.serie
            )}

                    </p>

                    <div class="form-buttons">

                        ${botonEditar("computadora", computadora.id)}

                        <button
                                type="button"
                                class="btn btn-secondary"
                                onclick="generarReporteComputadora(${Number(computadora.id)})">

                            Generar reporte

                        </button>

                    </div>


                    <!-- INFORMACIÓN EQUIPO -->

                    <div class="consulta-section">

                        <h3>
                            Información del equipo
                        </h3>


                        <div class="form-grid">


                            <div class="form-group">

                                <label>
                                    Marca
                                </label>

                                <span>
                                    ${esc(
                computadora.marca
            )}
                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    Modelo
                                </label>

                                <span>
                                    ${esc(
                computadora.modelo
            )}
                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    Procesador
                                </label>

                                <span>

                                    ${esc(
                computadora.tipoProcesador
            )}

                                    ${esc(
                computadora.generacionProcesador
            )}

                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    RAM
                                </label>

                                <span>
                                    ${esc(
                computadora.memoriaRAM
            )}
                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    Disco
                                </label>

                                <span>

                                    ${esc(
                computadora.tipoDisco
            )}

                                    -

                                    ${esc(
                computadora.capacidadDiscoGB
            )}
                                    GB

                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    Ubicación
                                </label>

                                <span>
                                    ${esc(
                computadora.ubicacion
            )}
                                </span>

                            </div>


                            <div class="form-group">

                                <label>
                                    Estado
                                </label>

                                <span>
                                    ${esc(
                computadora.estado
            )}
                                </span>

                            </div>


                        </div>

                    </div>


                    <!-- FUNCIONARIO ACTUAL -->

                    <div class="consulta-section">

                        <h3>
                            Funcionario actual
                        </h3>


                        <div class="estado-actual">

                            ${
                actual

                    ?

                    `

                                    <strong>

                                        ${esc(
                        actual.nombrePila
                    )}

                                    </strong>

                                    <br>

                                    Cédula:
                                    ${esc(
                        actual.cedula
                    )}

                                    <br>

                                    Cargo:
                                    ${esc(
                        actual.cargo
                    )}

                                    <br>

                                    Estado:
                                    ${esc(
                        actual.estado
                    )}

                                    `

                    :

                    "Sin funcionario asignado"

            }

                        </div>

                    </div>


                    <!-- HISTORIAL ASIGNACIONES -->

                    <div class="consulta-section">

                        <h3>
                            Historial de asignaciones
                        </h3>


                        <div class="table-container">

                            <table>

                                <thead>

                                <tr>

                                    <th>
                                        Funcionario
                                    </th>

                                    <th>
                                        Cédula
                                    </th>

                                    <th>
                                        Desde
                                    </th>

                                    <th>
                                        Hasta
                                    </th>

                                </tr>

                                </thead>


                                <tbody>

                                    ${historialHtml}

                                </tbody>

                            </table>

                        </div>

                    </div>


                    <!-- HISTORIAL MANTENIMIENTOS -->

                    <div class="consulta-section">

                        <h3>
                            Historial de mantenimientos
                        </h3>


                        <div class="table-container">

                            <table>

                                <thead>

                                <tr>

                                    <th>
                                        Fecha
                                    </th>

                                    <th>
                                        Tipo
                                    </th>

                                    <th>
                                        Diagnóstico
                                    </th>

                                    <th>
                                        Trabajo realizado
                                    </th>

                                    <th>
                                        Técnico
                                    </th>

                                    <th>
                                        Costo
                                    </th>

                                    <th>
                                        Acciones
                                    </th>

                                </tr>

                                </thead>


                                <tbody>

                                    ${mantenimientosHtml}

                                </tbody>

                            </table>

                        </div>

                    </div>


                </section>

            `;

        }
    );

}


// ========================================
// CONSULTA MANTENIMIENTO
// ========================================

document
    .getElementById("formMantenimiento")
    ?.addEventListener(
        "submit",
        async event => {

            event.preventDefault();


            const desde =
                document
                    .getElementById("desde")
                    .value;


            const hasta =
                document
                    .getElementById("hasta")
                    .value;


            if (!desde || !hasta) {

                mostrarMensaje(
                    "Seleccione las dos fechas.",
                    true
                );

                return;

            }


            if (desde > hasta) {

                mostrarMensaje(
                    "La fecha inicial no puede ser posterior a la fecha final.",
                    true
                );

                return;

            }


            try {

                mostrarMensaje(
                    "Consultando mantenimientos..."
                );


                const respuesta =
                    await apiFetch(
                        `/api/consultas/mantenimientos?desde=${encodeURIComponent(desde)}&hasta=${encodeURIComponent(hasta)}`
                    );


                if (!respuesta) {

                    return;

                }


                if (!respuesta.ok) {

                    await mostrarErrorRespuesta(
                        respuesta,
                        "No fue posible consultar los mantenimientos."
                    );


                    ocultarResultado(
                        "resultadoMantenimiento"
                    );


                    return;

                }


                const datos =
                    await respuesta.json();


                mostrarMantenimientosConsulta(
                    datos
                );


                document
                    .getElementById(
                        "resultadoMantenimiento"
                    )
                    .style.display =
                    "block";


                ocultarMensaje();

            } catch (error) {

                console.error(error);


                ocultarResultado(
                    "resultadoMantenimiento"
                );


                mostrarMensaje(
                    "Error de conexión con el servidor.",
                    true
                );

            }

        }
    );


// ========================================
// MOSTRAR MANTENIMIENTOS
// ========================================

function mostrarMantenimientosConsulta(
    mantenimientos
) {

    const tabla =
        document.getElementById(
            "tablaMantenimientosConsulta"
        );


    tabla.innerHTML = "";


    if (!mantenimientos.length) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="10"
                    class="sin-datos">

                    No existen mantenimientos
                    en el rango seleccionado.

                </td>

            </tr>

        `;

        return;

    }


    mantenimientos.forEach(
        mantenimiento => {

            tabla.innerHTML += `

                <tr>

                    <td>

                        ${formatearFecha(
                mantenimiento.fechaMantenimiento
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.nombreEquipo
            )}

                        <br>

                        <small>

                            Serie:
                            ${esc(
                mantenimiento.serieComputadora
            )}

                        </small>

                    </td>


                    <td>

                        ${esc(
                mantenimiento.tipoMantenimiento
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.diagnostico
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.trabajoRealizado
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.nombreUsuario
            )}

                    </td>


                    <td>

                        $${esc(
                mantenimiento.costo
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.estadoAnterior
            )}

                    </td>


                    <td>

                        ${esc(
                mantenimiento.estadoPosterior
            )}

                    </td>

                    <td>
                        ${botonEditar("mantenimiento", mantenimiento.id)}
                    </td>

                </tr>

            `;

        }
    );

}


// ========================================
// LIMPIAR FUNCIONARIO
// ========================================

document
    .getElementById("limpiarFuncionario")
    ?.addEventListener(
        "click",
        () => {

            document
                .getElementById(
                    "formFuncionario"
                )
                .reset();


            ocultarResultado(
                "resultadoFuncionario"
            );


            ocultarMensaje();

        }
    );


// ========================================
// LIMPIAR COMPUTADORA
// ========================================

document
    .getElementById("limpiarComputadora")
    ?.addEventListener(
        "click",
        () => {

            document
                .getElementById(
                    "formComputadora"
                )
                .reset();


            ocultarResultado(
                "resultadoComputadora"
            );


            document
                .getElementById(
                    "listaComputadorasConsulta"
                )
                .innerHTML = "";


            ocultarMensaje();

        }
    );


// ========================================
// LIMPIAR MANTENIMIENTO
// ========================================

document
    .getElementById("limpiarMantenimiento")
    ?.addEventListener(
        "click",
        () => {

            document
                .getElementById(
                    "formMantenimiento"
                )
                .reset();


            ocultarResultado(
                "resultadoMantenimiento"
            );


            document
                .getElementById(
                    "tablaMantenimientosConsulta"
                )
                .innerHTML = "";


            ocultarMensaje();

        }
    );


// ========================================
// FORMATEAR FECHA
// ========================================

function formatearFecha(
    fecha
) {

    if (!fecha) {

        return "N/A";

    }


    const fechaObj =
        new Date(fecha);


    return fechaObj.toLocaleString(
        "es-EC",
        {

            dateStyle: "short",

            timeStyle: "short"

        }
    );

}


// ========================================
// ESCAPAR HTML
// ========================================

function esc(valor) {

    if (
        valor === null ||
        valor === undefined ||
        valor === ""
    ) {

        return "N/A";

    }


    return String(valor)

        .replaceAll("&", "&amp;")

        .replaceAll("<", "&lt;")

        .replaceAll(">", "&gt;")

        .replaceAll(
            '"',
            "&quot;"
        )

        .replaceAll(
            "'",
            "&#039;"
        );

}


// ========================================
// ERROR
// ========================================

async function mostrarErrorRespuesta(
    respuesta,
    mensajePorDefecto
) {

    mostrarMensaje(
        await obtenerMensajeError(respuesta, mensajePorDefecto),
        true
    );

}


// ========================================
// MENSAJES
// ========================================

function mostrarMensaje(
    mensaje,
    error = false
) {

    const elemento =
        document.getElementById(
            "mensajeConsulta"
        );

    if (!elemento) return;

    elemento.textContent =
        mensaje;


    elemento.style.display =
        "block";


    elemento.style.borderLeft =
        error

            ? "5px solid #dc2626"

            : "5px solid #2563eb";

}


function ocultarMensaje() {

    const elemento =
        document.getElementById(
            "mensajeConsulta"
        );

    if (elemento) {
        elemento.style.display =
            "none";
    }

}


function ocultarResultado(
    id
) {

    const el = document.getElementById(id);
    if (el) {
        el.style.display = "none";
    }

}


// ========================================
// ACCIONES DESDE RESULTADOS
// ========================================

function botonEditar(
    tipo,
    id
) {

    if (!Number.isInteger(Number(id))) {
        return "";
    }


    return `

        <button
                type="button"
                class="btn btn-warning"
                onclick="editarDesdeConsulta('${tipo}', ${Number(id)})">

            Editar

        </button>

    `;

}


function editarDesdeConsulta(
    tipo,
    id
) {

    const rutas = {
        funcionario: "/funcionarios",
        computadora: "/computadoras",
        mantenimiento: "/mantenimientos"
    };


    const ruta = rutas[tipo];


    if (!ruta || !Number.isInteger(Number(id))) {
        return;
    }


    window.location.href =
        `${ruta}?editar=${encodeURIComponent(id)}`;

}


// ========================================
// REPORTES IMPRIMIBLES
// ========================================

function generarReporte(
    titulo,
    selector
) {

    const resultado =
        document.querySelector(selector);


    if (!resultado) {
        return;
    }


    const contenido = resultado.cloneNode(true);


    contenido
        .querySelectorAll("button, .acciones")
        .forEach(elemento => elemento.remove());


    const ventana = window.open("", "_blank");


    if (!ventana) {

        mostrarMensaje(
            "El navegador bloqueó la ventana del reporte. Permita las ventanas emergentes e intente nuevamente.",
            true
        );

        return;

    }


    ventana.document.write(`
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${esc(titulo)} - Sistema TIC</title>
            <link rel="stylesheet" href="/css/estilos.css">
            <style>
                body { padding: 32px; background: #fff; }
                .reporte-encabezado { margin-bottom: 24px; }
                .reporte-encabezado h1 { margin-bottom: 6px; }
                .reporte-fecha { color: #4b5563; }
                .sidebar, .topbar, .form-buttons { display: none !important; }
                .main-content { margin: 0; padding: 0; }
                @media print {
                    body { padding: 0; }
                    .panel, .consulta-result-card { break-inside: avoid; }
                }
            </style>
        </head>
        <body>
            <header class="reporte-encabezado">
                <h1>${esc(titulo)}</h1>
                <p class="reporte-fecha">Generado el ${esc(new Date().toLocaleString("es-EC"))}</p>
            </header>
            ${contenido.innerHTML}
            <script>window.addEventListener("load", () => window.print());</script>
        </body>
        </html>
    `);


    ventana.document.close();

}


function generarReporteComputadora(id) {

    generarReporte(
        "Reporte de computadora",
        `.resultado-computadora[data-computadora-id="${Number(id)}"]`
    );

}


document
    .getElementById("generarReporteFuncionario")
    ?.addEventListener(
        "click",
        () => generarReporte(
            "Reporte de funcionario",
            "#resultadoFuncionario"
        )
    );


document
    .getElementById("generarReporteMantenimiento")
    ?.addEventListener(
        "click",
        () => generarReporte(
            "Reporte de mantenimientos",
            "#resultadoMantenimiento"
        )
    );


// ========================================
// CONSULTAS RECIENTES
// ========================================

function guardarConsultaReciente(
    funcionario
) {

    const consultas =
        JSON.parse(
            localStorage.getItem(
                "ultimasConsultas"
            ) || "[]"
        );


    consultas.unshift({

        nombre:
            funcionario.nombrePila ||
            "Funcionario",

        tipo:
            `Consulta de funcionario - ${funcionario.cedula}`,

        icono:
            "♙",

        hora:
            new Date().toLocaleTimeString(
                "es-EC",
                {

                    hour: "2-digit",

                    minute: "2-digit"

                }
            )

    });


    localStorage.setItem(
        "ultimasConsultas",
        JSON.stringify(
            consultas.slice(0, 10)
        )
    );

}

const rol =
    localStorage.getItem("rol");

const menuAdministracion =
    document.getElementById(
        "menuAdministracion"
    );

if (
    menuAdministracion &&
    rol !== "ADMIN"
) {

    menuAdministracion.style.display =
        "none";
}
