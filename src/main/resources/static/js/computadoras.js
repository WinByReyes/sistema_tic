let modoEdicion = false;


// ========================================
// JWT Y SESIÓN
// ========================================

const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "/login";
}


// ========================================
// CERRAR SESIÓN
// ========================================

function cerrarSesion() {
    localStorage.removeItem("token");
    localStorage.removeItem("rol");
    localStorage.removeItem("usuario");
    localStorage.removeItem("nombre");
    window.location.href = "/login";
}


const botonLogout =
    document.getElementById("logout");


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
// CARGAR FUNCIONARIOS
// ========================================

async function cargarFuncionarios() {

    try {

        const respuesta =
            await apiFetch(
                "/api/funcionarios"
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los funcionarios"
            );

        }


        const funcionarios =
            await respuesta.json();


        const select =
            document.getElementById(
                "funcionario"
            );


        if (!select) {
            return;
        }


        select.innerHTML = `
            <option value="">
                Seleccione un funcionario
            </option>
        `;


        funcionarios.forEach(funcionario => {

            const opcion =
                document.createElement("option");


            opcion.value =
                funcionario.cedula;


            opcion.textContent =
                `${funcionario.cedula} - ${funcionario.nombrePila}`;


            select.appendChild(opcion);

        });


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar los funcionarios"
        );

    }

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


        const tabla =
            document.getElementById(
                "tablaComputadoras"
            );


        if (!tabla) {
            return;
        }


        tabla.innerHTML = "";


        computadoras.forEach(computadora => {

            const fila =
                document.createElement("tr");


            const nombreFuncionario =
                computadora.nombreFuncionario ||
                "Sin funcionario asignado";


            fila.innerHTML = `

                <td>
                    ${computadora.id}
                </td>

                <td>
                    ${nombreFuncionario}
                </td>

                <td>
                    ${computadora.serie || "—"}
                </td>

                <td>
                    ${computadora.nombreEquipo || "—"}
                </td>

                <td>
                    ${computadora.marca || "—"}
                </td>

                <td>
                    ${computadora.modelo || "—"}
                </td>

                <td>
                    ${computadora.estado || "—"}
                </td>

                <td>
                    ${computadora.ubicacion || "—"}
                </td>

                <td>

                    <div class="acciones">

                        <button
                            class="btn btn-warning"
                            onclick="editarComputadora(${computadora.id})"
                        >
                            Editar
                        </button>

                        <button
                            class="btn btn-danger"
                            onclick="eliminarComputadora(${computadora.id})"
                        >
                            Eliminar
                        </button>

                    </div>

                </td>

            `;


            tabla.appendChild(fila);

        });


    } catch (error) {

        console.error(error);

        alert(
            "Error al cargar las computadoras"
        );

    }

}


// ========================================
// CARGAR UN CATÁLOGO
// ========================================
async function cargarCatalogo(
    tipo,
    selectId,
    textoInicial
) {

    try {

        const respuesta =
            await apiFetch(
                `/api/catalogos/activos/${tipo}`
            );

        if (!respuesta) {
            return;
        }

        if (!respuesta.ok) {

            throw new Error(
                `No se pudo cargar el catálogo ${tipo}`
            );

        }

        const datos =
            await respuesta.json();

        const select =
            document.getElementById(selectId);

        if (!select) {
            return;
        }

        select.innerHTML = `
            <option value="">
                ${textoInicial}
            </option>
        `;

        datos.forEach(item => {

            const opcion =
                document.createElement("option");

            opcion.value =
                item.nombre;

            opcion.textContent =
                item.nombre;

            select.appendChild(opcion);

        });

        // ==========================================
        // OPCIÓN PERSONALIZADA
        // ==========================================

        const opcionOtro =
            document.createElement("option");

        opcionOtro.value =
            "__OTRO__";

        opcionOtro.textContent =
            "Otra opción...";

        select.appendChild(
            opcionOtro
        );

        configurarCampoOtro(
            selectId
        );

    } catch (error) {

        console.error(
            `Error cargando ${tipo}:`,
            error
        );

        alert(
            `No se pudo cargar el catálogo: ${tipo}`
        );

    }

}
// ========================================
// CONFIGURAR OPCIÓN "OTRO"
// ========================================

function configurarCampoOtro(selectId) {

    const select =
        document.getElementById(selectId);

    const input =
        document.getElementById(
            `${selectId}Otro`
        );

    if (!select || !input) {
        return;
    }

    select.addEventListener(
        "change",
        function () {

            if (
                this.value === "__OTRO__"
            ) {

                input.style.display =
                    "block";

                input.required =
                    true;

                input.focus();

            } else {

                input.style.display =
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
// OBTENER VALOR DEL CATÁLOGO
// ========================================

function obtenerValorCatalogo(
    selectId
) {

    const select =
        document.getElementById(
            selectId
        );

    if (!select) {
        return "";
    }

    if (
        select.value === "__OTRO__"
    ) {

        const input =
            document.getElementById(
                `${selectId}Otro`
            );

        if (!input) {
            return "";
        }

        return input.value.trim();

    }

    return select.value;

}
// ========================================
// CARGAR TODOS LOS CATÁLOGOS
// ========================================

async function cargarCatalogosComputadora() {

    await Promise.all([

        cargarCatalogo(
            "MARCA",
            "marca",
            "Seleccione la marca"
        ),

        cargarCatalogo(
            "TIPO_PROCESADOR",
            "tipoProcesador",
            "Seleccione el procesador"
        ),

        cargarCatalogo(
            "GENERACION_PROCESADOR",
            "generacionProcesador",
            "Seleccione la generación"
        ),

        cargarCatalogo(
            "RAM",
            "memoriaRAM",
            "Seleccione la RAM"
        ),

        cargarCatalogo(
            "TIPO_DISCO",
            "tipoDisco",
            "Seleccione el tipo de disco"
        ),

        cargarCatalogo(
            "SISTEMA_OPERATIVO",
            "sistemaOperativo",
            "Seleccione el sistema operativo"
        ),

        cargarCatalogo(
            "ESTADO",
            "estado",
            "Seleccione el estado"
        )

    ]);

}


// ========================================
// SELECCIONAR VALOR DEL CATÁLOGO
// ========================================
function seleccionarValorCatalogo(
    selectId,
    valor
) {

    const select =
        document.getElementById(
            selectId
        );

    const input =
        document.getElementById(
            `${selectId}Otro`
        );

    if (!select) {
        return;
    }

    if (
        !valor ||
        valor.trim() === ""
    ) {

        select.value = "";

        if (input) {

            input.value = "";

            input.style.display =
                "none";

            input.required =
                false;

        }

        return;

    }

    // ========================================
    // BUSCAR SI EXISTE EN EL CATÁLOGO
    // ========================================

    const opcionExistente =
        Array.from(
            select.options
        ).find(
            opcion =>
                opcion.value === valor
        );

    if (opcionExistente) {

        select.value =
            valor;

        if (input) {

            input.value = "";

            input.style.display =
                "none";

            input.required =
                false;

        }

        return;

    }

    // ========================================
    // SI NO EXISTE -> VALOR PERSONALIZADO
    // ========================================

    select.value =
        "__OTRO__";

    if (input) {

        input.value =
            valor;

        input.style.display =
            "block";

        input.required =
            true;

    }

}
// ========================================
// ABRIR FORMULARIO
// ========================================

async function abrirFormulario() {

    modoEdicion = false;


    document
        .getElementById("computadoraForm")
        .reset();


    document
        .getElementById("computadoraId")
        .value = "";


    document
        .getElementById("tituloFormulario")
        .textContent =
        "Nueva computadora";


    // ====================================
    // CARGAR FUNCIONARIOS
    // ====================================

    await cargarFuncionarios();


    // ====================================
    // CARGAR CATÁLOGOS
    // ====================================

    await cargarCatalogosComputadora();


    // ====================================
    // MOSTRAR FORMULARIO
    // ====================================

    document
        .getElementById("formularioComputadora")
        .style.display =
        "block";

}


// ========================================
// CERRAR FORMULARIO
// ========================================

function cerrarFormulario() {

    document
        .getElementById("formularioComputadora")
        .style.display =
        "none";

}


// ========================================
// CREAR / ACTUALIZAR
// ========================================

const formularioComputadora =
    document.getElementById(
        "computadoraForm"
    );


if (formularioComputadora) {

    formularioComputadora.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();


            const id =
                document.getElementById(
                    "computadoraId"
                ).value;


            // ====================================
            // OBTENER DATOS
            // ====================================

            const datos = {

                // ====================================
                // FUNCIONARIO
                // ====================================

                cedulaFuncionario:
                    document
                        .getElementById(
                            "funcionario"
                        )
                        .value || null,


                // ====================================
                // DATOS BÁSICOS
                // ====================================

                serie:
                    document
                        .getElementById(
                            "serie"
                        )
                        .value
                        .trim(),


                nombreEquipo:
                    document
                        .getElementById(
                            "nombreEquipo"
                        )
                        .value
                        .trim(),


                procedencia:
                    document
                        .getElementById(
                            "procedencia"
                        )
                        .value
                        .trim(),


                tipo:
                    document
                        .getElementById(
                            "tipo"
                        )
                        .value
                        .trim(),


                // ====================================
                // MARCA
                // ====================================

                marca:
                    obtenerValorCatalogo("marca"),

                // ====================================
                // MODELO
                // ====================================

                modelo:
                    document
                        .getElementById(
                            "modelo"
                        )
                        .value
                        .trim(),


                // ====================================
                // ESTADO
                // ====================================

                estado:
                    obtenerValorCatalogo("estado"),


                // ====================================
                // PROCESADOR
                // ====================================

                tipoProcesador:
                    obtenerValorCatalogo("tipoProcesador"),


                // ====================================
                // GENERACIÓN
                // ====================================

                generacionProcesador:
                    obtenerValorCatalogo("generacionProcesador"),


                // ====================================
                // VELOCIDAD
                // ====================================

                velocidadProcesador:
                    document
                        .getElementById(
                            "velocidadProcesador"
                        )
                        .value
                        .trim(),


                // ====================================
                // RAM
                // ====================================

                memoriaRAM:
                    obtenerValorCatalogo("memoriaRAM"),

                // ====================================
                // DISCO
                // ====================================

                tipoDisco:
                    obtenerValorCatalogo("tipoDisco"),

                capacidadDiscoGB:
                    document
                        .getElementById(
                            "capacidadDiscoGB"
                        )
                        .value
                        .trim(),


                // ====================================
                // SISTEMA OPERATIVO
                // ====================================
                sistemaOperativo:
                    obtenerValorCatalogo("sistemaOperativo"),


                // ====================================
                // OFFICE
                // ====================================

                office:
                    document
                        .getElementById(
                            "office"
                        )
                        .value
                        .trim(),


                // ====================================
                // UBICACIÓN
                // ====================================

                ubicacion:
                    document
                        .getElementById(
                            "ubicacion"
                        )
                        .value
                        .trim()

            };


            // ========================================
            // VALIDAR CAMPOS OBLIGATORIOS
            // ========================================

            if (

                !datos.marca ||

                !datos.tipoProcesador ||

                !datos.generacionProcesador ||

                !datos.memoriaRAM ||

                !datos.tipoDisco ||

                !datos.sistemaOperativo ||

                !datos.estado

            ) {

                alert(
                    "Complete todos los campos obligatorios."
                );

                return;

            }


            try {

                let respuesta;


                // ====================================
                // CREAR
                // ====================================

                if (!modoEdicion) {

                    respuesta =
                        await apiFetch(
                            "/api/computadoras",
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
                            `/api/computadoras/${id}`,
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


                // ====================================
                // RESPUESTA CON ERROR
                // ====================================

                if (!respuesta.ok) {

                    const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar la computadora");
                    alert(mensaje);
                    return;

                }


                // ====================================
                // ÉXITO
                // ====================================

                alert(
                    modoEdicion
                        ? "Computadora actualizada correctamente"
                        : "Computadora creada correctamente"
                );


                cerrarFormulario();


                await cargarComputadoras();


            } catch (error) {

                console.error(error);

                alert(
                    "Error de conexión con el servidor"
                );

            }

        }
    );

}


// ========================================
// EDITAR COMPUTADORA
// ========================================

async function editarComputadora(id) {

    try {

        // ====================================
        // OBTENER COMPUTADORA
        // ====================================

        const respuesta =
            await apiFetch(
                `/api/computadoras/${id}`
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "Computadora no encontrada"
            );

        }


        const computadora =
            await respuesta.json();


        modoEdicion = true;


        // ====================================
        // ID
        // ====================================

        document
            .getElementById("computadoraId")
            .value =
            computadora.id;


        // ====================================
        // CARGAR FUNCIONARIOS
        // ====================================

        await cargarFuncionarios();


        // ====================================
        // SELECCIONAR FUNCIONARIO
        // ====================================

        if (computadora.funcionario) {

            document
                .getElementById("funcionario")
                .value =
                computadora
                    .funcionario
                    .cedula;

        }


        // ====================================
        // CARGAR CATÁLOGOS
        // ====================================

        await cargarCatalogosComputadora();


        // ====================================
        // DATOS BÁSICOS
        // ====================================

        document
            .getElementById("serie")
            .value =
            computadora.serie || "";


        document
            .getElementById("nombreEquipo")
            .value =
            computadora.nombreEquipo || "";


        document
            .getElementById("procedencia")
            .value =
            computadora.procedencia || "";


        document
            .getElementById("tipo")
            .value =
            computadora.tipo || "";


        document
            .getElementById("modelo")
            .value =
            computadora.modelo || "";


        document
            .getElementById("velocidadProcesador")
            .value =
            computadora.velocidadProcesador || "";


        document
            .getElementById("capacidadDiscoGB")
            .value =
            computadora.capacidadDiscoGB || "";


        document
            .getElementById("office")
            .value =
            computadora.office || "";


        document
            .getElementById("ubicacion")
            .value =
            computadora.ubicacion || "";


        // ====================================
        // MARCA
        // ====================================

        seleccionarValorCatalogo(
            "marca",
            computadora.marca
        );


        // ====================================
        // ESTADO
        // ====================================

        seleccionarValorCatalogo(
            "estado",
            computadora.estado
        );


        // ====================================
        // TIPO PROCESADOR
        // ====================================

        seleccionarValorCatalogo(
            "tipoProcesador",
            computadora.tipoProcesador
        );


        // ====================================
        // GENERACIÓN
        // ====================================

        seleccionarValorCatalogo(
            "generacionProcesador",
            computadora.generacionProcesador
        );


        // ====================================
        // RAM
        // ====================================

        seleccionarValorCatalogo(
            "memoriaRAM",
            computadora.memoriaRAM
        );


        // ====================================
        // TIPO DISCO
        // ====================================

        seleccionarValorCatalogo(
            "tipoDisco",
            computadora.tipoDisco
        );


        // ====================================
        // SISTEMA OPERATIVO
        // ====================================

        seleccionarValorCatalogo(
            "sistemaOperativo",
            computadora.sistemaOperativo
        );


        // ====================================
        // TÍTULO
        // ====================================

        document
            .getElementById("tituloFormulario")
            .textContent =
            "Editar computadora";


        // ====================================
        // MOSTRAR FORMULARIO
        // ====================================

        document
            .getElementById("formularioComputadora")
            .style.display =
            "block";


    } catch (error) {

        console.error(error);

        alert(
            "Error al obtener la computadora"
        );

    }

}


// ========================================
// ELIMINAR COMPUTADORA
// ========================================

async function eliminarComputadora(id) {

    if (
        !confirm(
            "¿Está seguro de eliminar esta computadora?"
        )
    ) {

        return;

    }


    try {

        const respuesta =
            await apiFetch(
                `/api/computadoras/${id}`,
                {

                    method: "DELETE"

                }
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar la computadora");
            alert(mensaje);
            return;

        }


        alert(
            "Computadora eliminada correctamente"
        );


        await cargarComputadoras();


    } catch (error) {

        console.error(error);

        alert(
            "Error de conexión con el servidor"
        );

    }

}


// ========================================
// CONTROL DE MENÚ SEGÚN ROL
// ========================================

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


// ========================================
// INICIO
// ========================================

async function iniciarPagina() {

    await cargarCatalogosComputadora();

    await cargarComputadoras();


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


    await editarComputadora(Number(idEdicion));

}


iniciarPagina();
