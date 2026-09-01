let modoEdicion = false;


// ========================================
// JWT
// ========================================

const token =
    localStorage.getItem("token");


if (!token) {

    window.location.href =
        "/login";

}


// ========================================
// CERRAR SESIÓN
// ========================================

function cerrarSesion() {

    localStorage.removeItem("token");
    localStorage.removeItem("rol");
    localStorage.removeItem("usuario");
    localStorage.removeItem("nombre");

    window.location.href =
        "/login";

}


const botonLogout =
    document.getElementById(
        "logout"
    );


if (botonLogout) {

    botonLogout.addEventListener(
        "click",
        function(event) {

            event.preventDefault();

            cerrarSesion();

        }
    );

}


// ========================================
// CARGAR MANTENIMIENTOS
// ========================================

async function cargarMantenimientos() {

    try {

        const respuesta =
            await apiFetch(
                "/api/mantenimientos"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los mantenimientos"
            );

        }


        const mantenimientos =
            await respuesta.json();


        const tabla =
            document.getElementById(
                "tablaMantenimientos"
            );


        tabla.innerHTML = "";


        mantenimientos.forEach(
            mantenimiento => {

                const fila =
                    document.createElement(
                        "tr"
                    );


                fila.innerHTML = `

                    <td>
                        ${mantenimiento.id}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.nombreEquipo || "-"
                )}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.nombreResponsable || "-"
                )}
                    </td>

                    <td>
                        ${formatearFecha(
                    mantenimiento.fechaMantenimiento
                )}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.tipoMantenimiento || "-"
                )}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.estadoMantenimiento || "-"
                )}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.diagnostico || "-"
                )}
                    </td>

                    <td>
                        $${mantenimiento.costo ?? "0.00"}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.estadoAnterior || "-"
                )}
                    </td>

                    <td>
                        ${escapeHtml(
                    mantenimiento.estadoPosterior || "-"
                )}
                    </td>

                    <td>

                        <div class="acciones">

                            <button
                                class="btn btn-warning"
                                onclick="editarMantenimiento(
                                    ${mantenimiento.id}
                                )">

                                Editar

                            </button>


                            <button
                                class="btn btn-danger"
                                onclick="eliminarMantenimiento(
                                    ${mantenimiento.id}
                                )">

                                Eliminar

                            </button>

                        </div>

                    </td>

                `;


                tabla.appendChild(
                    fila
                );

            }
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los mantenimientos"
        );

    }

}


// ========================================
// ESCAPAR HTML
// ========================================

function escapeHtml(text) {

    return String(text)

        .replaceAll(
            "&",
            "&amp;"
        )

        .replaceAll(
            "<",
            "&lt;"
        )

        .replaceAll(
            ">",
            "&gt;"
        )

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
// FORMATEAR FECHA
// ========================================

function formatearFecha(fecha) {

    if (!fecha) {

        return "-";

    }


    return new Date(fecha)
        .toLocaleString(
            "es-EC"
        );

}


// ========================================
// CARGAR COMPUTADORAS
// ========================================

async function cargarComputadoras() {

    try {

        const respuesta =
            await apiFetch(
                "/api/computadoras"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar las computadoras"
            );

        }


        const computadoras =
            await respuesta.json();


        const select =
            document.getElementById(
                "computadoraId"
            );


        select.innerHTML = `

            <option value="">

                Seleccione una computadora

            </option>

        `;


        computadoras.forEach(
            computadora => {

                const option =
                    document.createElement(
                        "option"
                    );


                option.value =
                    computadora.id;


                option.textContent =
                    `${computadora.nombreEquipo}
                    - ${computadora.serie}`;


                select.appendChild(
                    option
                );

            }
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar las computadoras"
        );

    }

}


// ========================================
// CARGAR RESPONSABLES
// ========================================

async function cargarResponsables() {

    try {

        const respuesta =
            await apiFetch(
                "/api/responsables/activos"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los responsables"
            );

        }


        const responsables =
            await respuesta.json();


        const select =
            document.getElementById(
                "responsableId"
            );


        select.innerHTML = `

            <option value="">

                Seleccione un responsable

            </option>

        `;

        agregarOpcionOtro(select);

        responsables.forEach(
            responsable => {

                const option =
                    document.createElement(
                        "option"
                    );


                option.value =
                    responsable.id;


                option.textContent =
                    responsable.cargo

                        ? `${responsable.nombre}
                           - ${responsable.cargo}`

                        : responsable.nombre;


                select.appendChild(
                    option
                );

            }
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los responsables"
        );

    }

}


// ========================================
// CARGAR TIPOS DE MANTENIMIENTO
// ========================================

async function cargarTiposMantenimiento() {

    try {

        const respuesta =
            await apiFetch(
                "/api/catalogos/activos/TIPO_MANTENIMIENTO"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los tipos de mantenimiento"
            );

        }


        const catalogos =
            await respuesta.json();


        const select =
            document.getElementById(
                "tipoMantenimiento"
            );


        select.innerHTML = `

            <option value="">

                Seleccione un tipo

            </option>

        `;


        catalogos.forEach(
            catalogo => {

                const option =
                    document.createElement(
                        "option"
                    );


                option.value =
                    catalogo.nombre;


                option.textContent =
                    catalogo.nombre;


                select.appendChild(
                    option
                );

            }
        );


        // ==================================
        // OPCIÓN OTRO
        // ==================================

        agregarOpcionOtro(
            select
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los tipos de mantenimiento"
        );

    }

}


// ========================================
// CARGAR ESTADO DEL MANTENIMIENTO
// ========================================

async function cargarEstadoMantenimiento() {

    try {

        const respuesta =
            await apiFetch(
                "/api/catalogos/activos/ESTADO_MANTENIMIENTO"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los estados de mantenimiento"
            );

        }


        const estados =
            await respuesta.json();


        const select =
            document.getElementById(
                "estadoMantenimiento"
            );


        select.innerHTML = `

            <option value="">

                Seleccione un estado

            </option>

        `;


        estados.forEach(
            estado => {

                const option =
                    document.createElement(
                        "option"
                    );


                option.value =
                    estado.nombre;


                option.textContent =
                    estado.nombre;


                select.appendChild(
                    option
                );

            }
        );


        // ==================================
        // OPCIÓN OTRO
        // ==================================

        agregarOpcionOtro(
            select
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los estados de mantenimiento"
        );

    }

}


// ========================================
// CARGAR ESTADOS DE COMPUTADORA
// ========================================

async function cargarEstadosComputadora() {

    try {

        const respuesta =
            await apiFetch(
                "/api/catalogos/activos/ESTADO"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los estados de computadora"
            );

        }


        const estados =
            await respuesta.json();


        const selectAnterior =
            document.getElementById(
                "estadoAnterior"
            );


        const selectPosterior =
            document.getElementById(
                "estadoPosterior"
            );


        selectAnterior.innerHTML = `

            <option value="">

                Seleccione un estado

            </option>

        `;


        selectPosterior.innerHTML = `

            <option value="">

                Seleccione un estado

            </option>

        `;


        estados.forEach(
            estado => {

                const optionAnterior =
                    document.createElement(
                        "option"
                    );


                optionAnterior.value =
                    estado.nombre;


                optionAnterior.textContent =
                    estado.nombre;


                selectAnterior.appendChild(
                    optionAnterior
                );


                const optionPosterior =
                    document.createElement(
                        "option"
                    );


                optionPosterior.value =
                    estado.nombre;


                optionPosterior.textContent =
                    estado.nombre;


                selectPosterior.appendChild(
                    optionPosterior
                );

            }
        );


        // ==================================
        // OTRO ANTERIOR
        // ==================================

        agregarOpcionOtro(
            selectAnterior
        );


        // ==================================
        // OTRO POSTERIOR
        // ==================================

        agregarOpcionOtro(
            selectPosterior
        );


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los estados de computadora"
        );

    }

}


// ========================================
// AGREGAR OPCIÓN "OTRO"
// ========================================

function agregarOpcionOtro(select) {

    if (!select) {
        return;
    }


    const existe =
        Array.from(
            select.options
        ).some(
            option =>
                option.value === "OTRO"
        );


    if (existe) {
        return;
    }


    const otro =
        document.createElement(
            "option"
        );


    otro.value =
        "OTRO";


    otro.textContent =
        "Otro";


    select.appendChild(
        otro
    );

}


// ========================================
// MOSTRAR USUARIO REGISTRADOR
// ========================================

function mostrarUsuarioRegistrador() {

    const nombre =
        localStorage.getItem(
            "nombre"
        );


    const usuario =
        localStorage.getItem(
            "usuario"
        );


    const campo =
        document.getElementById(
            "usuarioRegistrador"
        );


    if (!campo) {
        return;
    }


    campo.value =
        nombre

            ? `${nombre} (${usuario})`

            : usuario || "";

}


// ========================================
// CONFIGURAR OPCIÓN "OTRO"
// ========================================

function configurarOpcionOtro(
    selectId,
    containerId,
    inputId
) {

    const select =
        document.getElementById(
            selectId
        );


    const container =
        document.getElementById(
            containerId
        );


    const input =
        document.getElementById(
            inputId
        );


    if (
        !select ||
        !container ||
        !input
    ) {

        return;

    }


    select.addEventListener(
        "change",
        function() {

            if (
                this.value === "OTRO"
            ) {

                container.style.display =
                    "block";


                input.required =
                    true;


                input.focus();

            } else {

                container.style.display =
                    "none";


                input.required =
                    false;


                input.value =
                    "";

            }

        }
    );

}


// ========================================
// OCULTAR CAMPOS "OTRO"
// ========================================

function ocultarCamposOtro() {

    const configuraciones = [

        {
            container:
                "tipoMantenimientoOtroContainer",

            input:
                "tipoMantenimientoOtro"
        },

        {
            container:
                "estadoMantenimientoOtroContainer",

            input:
                "estadoMantenimientoOtro"
        },

        {
            container:
                "estadoAnteriorOtroContainer",

            input:
                "estadoAnteriorOtro"
        },

        {
            container:
                "estadoPosteriorOtroContainer",

            input:
                "estadoPosteriorOtro"
        },

        {
            container: "responsableOtroContainer",
            input: "responsableOtro"
        }

    ];



    configuraciones.forEach(
        configuracion => {

            const container =
                document.getElementById(
                    configuracion.container
                );


            const input =
                document.getElementById(
                    configuracion.input
                );


            if (container) {

                container.style.display =
                    "none";

            }


            if (input) {

                input.value =
                    "";

                input.required =
                    false;

            }

        }
    );

}


// ========================================
// OBTENER VALOR DE CATÁLOGO / OTRO
// ========================================

function obtenerValorCatalogo(
    selectId,
    inputId
) {

    const select =
        document.getElementById(
            selectId
        );


    if (!select) {
        return "";
    }


    let valor =
        select.value;


    if (valor === "OTRO") {

        const input =
            document.getElementById(
                inputId
            );


        if (!input) {
            return "";
        }


        valor =
            input.value.trim();

    }


    return valor;

}


// ========================================
// SELECCIONAR VALOR AL EDITAR
// ========================================

function seleccionarValorCatalogo(
    selectId,
    inputId,
    containerId,
    valor
) {

    const select =
        document.getElementById(
            selectId
        );


    const input =
        document.getElementById(
            inputId
        );


    const container =
        document.getElementById(
            containerId
        );


    if (!select) {
        return;
    }


    const valorNormalizado =
        valor == null
            ? ""
            : String(valor).trim();


    const existe =
        Array.from(
            select.options
        ).some(
            option =>
                option.value ===
                valorNormalizado
        );


    if (
        valorNormalizado &&
        existe
    ) {

        select.value =
            valorNormalizado;


        if (container) {

            container.style.display =
                "none";

        }


        if (input) {

            input.value =
                "";

            input.required =
                false;

        }


        return;

    }


    if (valorNormalizado) {

        select.value =
            "OTRO";


        if (container) {

            container.style.display =
                "block";

        }


        if (input) {

            input.value =
                valorNormalizado;

            input.required =
                true;

        }


    } else {

        select.value =
            "";


        if (container) {

            container.style.display =
                "none";

        }


        if (input) {

            input.value =
                "";

            input.required =
                false;

        }

    }

}


// ========================================
// ABRIR FORMULARIO
// ========================================

async function abrirFormulario() {

    modoEdicion =
        false;


    const formulario =
        document.getElementById(
            "mantenimientoForm"
        );


    formulario.reset();


    ocultarCamposOtro();


    document
        .getElementById(
            "mantenimientoId"
        )
        .value =
        "";


    document
        .getElementById(
            "tituloFormulario"
        )
        .textContent =
        "Nuevo mantenimiento";


    await cargarComputadoras();

    await cargarResponsables();

    await cargarTiposMantenimiento();

    await cargarEstadoMantenimiento();

    await cargarEstadosComputadora();


    mostrarUsuarioRegistrador();


    document
        .getElementById(
            "formularioMantenimiento"
        )
        .style.display =
        "block";

}


// ========================================
// CERRAR FORMULARIO
// ========================================

function cerrarFormulario() {

    document
        .getElementById(
            "formularioMantenimiento"
        )
        .style.display =
        "none";


    ocultarCamposOtro();

}


// ========================================
// CONFIGURAR LISTENERS "OTRO"
// ========================================

configurarOpcionOtro(
    "tipoMantenimiento",
    "tipoMantenimientoOtroContainer",
    "tipoMantenimientoOtro"
);

configurarOpcionOtro(
    "responsableId",
    "responsableOtroContainer",
    "responsableOtro"
);


configurarOpcionOtro(
    "estadoMantenimiento",
    "estadoMantenimientoOtroContainer",
    "estadoMantenimientoOtro"
);


configurarOpcionOtro(
    "estadoAnterior",
    "estadoAnteriorOtroContainer",
    "estadoAnteriorOtro"
);


configurarOpcionOtro(
    "estadoPosterior",
    "estadoPosteriorOtroContainer",
    "estadoPosteriorOtro"
);


// ========================================
// CREAR / ACTUALIZAR
// ========================================

const formularioMantenimiento =
    document.getElementById(
        "mantenimientoForm"
    );


if (formularioMantenimiento) {

    formularioMantenimiento
        .addEventListener(
            "submit",
            async function(event) {

                event.preventDefault();


                const id =
                    document
                        .getElementById(
                            "mantenimientoId"
                        )
                        .value;


                // ====================================
                // OBTENER VALORES
                // ====================================

                const tipoMantenimiento =
                    obtenerValorCatalogo(
                        "tipoMantenimiento",
                        "tipoMantenimientoOtro"
                    );


                const estadoMantenimiento =
                    obtenerValorCatalogo(
                        "estadoMantenimiento",
                        "estadoMantenimientoOtro"
                    );


                const estadoAnterior =
                    obtenerValorCatalogo(
                        "estadoAnterior",
                        "estadoAnteriorOtro"
                    );


                const estadoPosterior =
                    obtenerValorCatalogo(
                        "estadoPosterior",
                        "estadoPosteriorOtro"
                    );


                const responsableSeleccionado =
                    document.getElementById("responsableId").value;

                const esResponsableManual =
                    responsableSeleccionado === "OTRO";

                const responsableIdEnviar =
                    esResponsableManual || !responsableSeleccionado
                        ? null
                        : Number(responsableSeleccionado);

                const responsableManualEnviar =
                    esResponsableManual
                        ? document.getElementById("responsableOtro").value.trim()
                        : null;

                // ====================================
                // DATOS
                // ====================================

                const datos = {

                    computadoraId:
                        Number(
                            document
                                .getElementById(
                                    "computadoraId"
                                )
                                .value
                        ),


                    responsableId: responsableIdEnviar,
                    responsableManual: responsableManualEnviar,


                    tipoMantenimiento:
                    tipoMantenimiento,


                    estadoMantenimiento:
                    estadoMantenimiento,


                    diagnostico:
                        document
                            .getElementById(
                                "diagnostico"
                            )
                            .value
                            .trim(),


                    trabajoRealizado:
                        document
                            .getElementById(
                                "trabajoRealizado"
                            )
                            .value
                            .trim(),


                    costo:
                        Number(
                            document
                                .getElementById(
                                    "costo"
                                )
                                .value
                        ),


                    estadoAnterior:
                    estadoAnterior,


                    estadoPosterior:
                    estadoPosterior,


                    observaciones:
                        document
                            .getElementById(
                                "observaciones"
                            )
                            .value
                            .trim(),


                    fechaMantenimiento:
                        document
                            .getElementById(
                                "fechaMantenimiento"
                            )
                            .value

                };


                // ====================================
                // VALIDACIONES
                // ====================================

                if (!datos.computadoraId) {

                    alert(
                        "Debe seleccionar una computadora."
                    );

                    return;

                }


                if (!datos.responsableId && !datos.responsableManual) {
                    alert("Debe seleccionar un responsable o escribir su nombre.");
                    return;
                }


                if (!datos.tipoMantenimiento) {

                    alert(
                        "Debe seleccionar o especificar un tipo de mantenimiento."
                    );

                    return;

                }


                if (!datos.estadoMantenimiento) {

                    alert(
                        "Debe seleccionar o especificar un estado de mantenimiento."
                    );

                    return;

                }


                if (!datos.diagnostico) {

                    alert(
                        "Debe ingresar el diagnóstico."
                    );

                    return;

                }


                if (!datos.trabajoRealizado) {

                    alert(
                        "Debe ingresar el trabajo realizado."
                    );

                    return;

                }


                if (
                    isNaN(datos.costo) ||
                    datos.costo < 0
                ) {

                    alert(
                        "Debe ingresar un costo válido."
                    );

                    return;

                }


                if (!datos.estadoAnterior) {

                    alert(
                        "Debe seleccionar o especificar el estado anterior de la computadora."
                    );

                    return;

                }


                if (!datos.estadoPosterior) {

                    alert(
                        "Debe seleccionar o especificar el estado posterior de la computadora."
                    );

                    return;

                }


                if (!datos.observaciones) {

                    alert(
                        "Debe ingresar las observaciones."
                    );

                    return;

                }


                if (!datos.fechaMantenimiento) {

                    alert(
                        "Debe ingresar la fecha del mantenimiento."
                    );

                    return;

                }


                // ====================================
                // ENVIAR
                // ====================================

                try {

                    let respuesta;


                    // ====================================
                    // CREAR
                    // ====================================

                    if (!modoEdicion) {

                        respuesta =
                            await apiFetch(
                                "/api/mantenimientos",
                                {

                                    method: "POST",

                                    body:
                                        JSON.stringify(
                                            datos
                                        )

                                }
                            );

                    }


                        // ====================================
                        // ACTUALIZAR
                    // ====================================

                    else {

                        respuesta =
                            await apiFetch(
                                `/api/mantenimientos/${id}`,
                                {

                                    method: "PUT",

                                    body:
                                        JSON.stringify(
                                            datos
                                        )

                                }
                            );

                    }


                    if (!respuesta) {
                        return;
                    }


                    if (!respuesta.ok) {

                        const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el mantenimiento");
                        alert(mensaje);
                        return;

                    }


                    alert(

                        modoEdicion

                            ? "Mantenimiento actualizado correctamente."

                            : "Mantenimiento creado correctamente."

                    );


                    cerrarFormulario();


                    await cargarMantenimientos();


                } catch (error) {

                    console.error(error);

                    alert(
                        "Error de conexión con el servidor."
                    );

                }

            }
        );

}


// ========================================
// EDITAR
// ========================================

async function editarMantenimiento(id) {

    try {

        const respuesta =
            await apiFetch(
                `/api/mantenimientos/${id}`
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "Mantenimiento no encontrado"
            );

        }


        const mantenimiento =
            await respuesta.json();


        modoEdicion =
            true;


        ocultarCamposOtro();


        await cargarComputadoras();

        await cargarResponsables();

        await cargarTiposMantenimiento();

        await cargarEstadoMantenimiento();

        await cargarEstadosComputadora();


        // ========================================
        // ID
        // ========================================

        document
            .getElementById(
                "mantenimientoId"
            )
            .value =
            mantenimiento.id;


        // ========================================
        // COMPUTADORA
        // ========================================

        document
            .getElementById(
                "computadoraId"
            )
            .value =
            mantenimiento.computadoraId;


        // ========================================
        // RESPONSABLE
        // ========================================

        const selectResponsable = document.getElementById("responsableId");
        const inputResponsableOtro = document.getElementById("responsableOtro");
        const containerResponsableOtro = document.getElementById("responsableOtroContainer");

        if (mantenimiento.responsableId) {

            selectResponsable.value = mantenimiento.responsableId;

        } else {

            selectResponsable.value = "OTRO";

            if (containerResponsableOtro) containerResponsableOtro.style.display = "block";

            if (inputResponsableOtro) {
                inputResponsableOtro.required = true;
                inputResponsableOtro.value = mantenimiento.nombreResponsable || "";
            }
        }


        // ========================================
        // TIPO
        // ========================================

        seleccionarValorCatalogo(

            "tipoMantenimiento",

            "tipoMantenimientoOtro",

            "tipoMantenimientoOtroContainer",

            mantenimiento.tipoMantenimiento

        );


        // ========================================
        // ESTADO DEL MANTENIMIENTO
        // ========================================

        seleccionarValorCatalogo(

            "estadoMantenimiento",

            "estadoMantenimientoOtro",

            "estadoMantenimientoOtroContainer",

            mantenimiento.estadoMantenimiento

        );


        // ========================================
        // DIAGNÓSTICO
        // ========================================

        document
            .getElementById(
                "diagnostico"
            )
            .value =
            mantenimiento.diagnostico || "";


        // ========================================
        // TRABAJO REALIZADO
        // ========================================

        document
            .getElementById(
                "trabajoRealizado"
            )
            .value =
            mantenimiento.trabajoRealizado || "";


        // ========================================
        // COSTO
        // ========================================

        document
            .getElementById(
                "costo"
            )
            .value =
            mantenimiento.costo ?? "";


        // ========================================
        // ESTADO ANTERIOR
        // ========================================

        seleccionarValorCatalogo(

            "estadoAnterior",

            "estadoAnteriorOtro",

            "estadoAnteriorOtroContainer",

            mantenimiento.estadoAnterior

        );


        // ========================================
        // ESTADO POSTERIOR
        // ========================================

        seleccionarValorCatalogo(

            "estadoPosterior",

            "estadoPosteriorOtro",

            "estadoPosteriorOtroContainer",

            mantenimiento.estadoPosterior

        );


        // ========================================
        // OBSERVACIONES
        // ========================================

        document
            .getElementById(
                "observaciones"
            )
            .value =
            mantenimiento.observaciones || "";


        // ========================================
        // FECHA
        // ========================================

        if (
            mantenimiento.fechaMantenimiento
        ) {

            document
                .getElementById(
                    "fechaMantenimiento"
                )
                .value =
                mantenimiento
                    .fechaMantenimiento
                    .slice(0, 16);

        } else {

            document
                .getElementById(
                    "fechaMantenimiento"
                )
                .value =
                "";

        }


        // ========================================
        // USUARIO
        // ========================================

        mostrarUsuarioRegistrador();


        // ========================================
        // TÍTULO
        // ========================================

        document
            .getElementById(
                "tituloFormulario"
            )
            .textContent =
            "Editar mantenimiento";


        // ========================================
        // MOSTRAR FORMULARIO
        // ========================================

        document
            .getElementById(
                "formularioMantenimiento"
            )
            .style.display =
            "block";


    } catch (error) {

        console.error(error);

        alert(
            "Error al obtener el mantenimiento."
        );

    }

}


// ========================================
// ELIMINAR
// ========================================

async function eliminarMantenimiento(id) {

    if (
        !confirm(
            "¿Está seguro de eliminar este mantenimiento?"
        )
    ) {

        return;

    }


    try {

        const respuesta =
            await apiFetch(
                `/api/mantenimientos/${id}`,
                {

                    method: "DELETE"

                }
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar el mantenimiento");
            alert(mensaje);
            return;

        }


        alert(
            "Mantenimiento eliminado correctamente."
        );


        await cargarMantenimientos();


    } catch (error) {

        console.error(error);

        alert(
            "Error de conexión con el servidor."
        );

    }

}


// ========================================
// CONTROL DE ADMINISTRACIÓN
// ========================================

const rol =
    localStorage.getItem(
        "rol"
    );


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


// ========================================
// INICIO
// ========================================

async function iniciarPagina() {

    await cargarMantenimientos();


    const idEdicion =
        new URLSearchParams(window.location.search)
            .get("editar");


    if (!/^\d+$/.test(idEdicion || "")) {
        return;
    }


    window.history.replaceState(
        {},
        document.title,
        window.location.pathname
    );


    await editarMantenimiento(Number(idEdicion));

}


iniciarPagina();
