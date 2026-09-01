let modoEdicion = false;


// ========================================
// JWT
// ========================================

const token = localStorage.getItem("token");


// Si no existe token, volver al login
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


        const tabla =
            document.getElementById(
                "tablaFuncionarios"
            );


        tabla.innerHTML = "";


        funcionarios.forEach(funcionario => {

            const fila =
                document.createElement("tr");


            fila.innerHTML = `

                <td>${funcionario.id}</td>

                <td>${funcionario.cedula}</td>

                <td>${funcionario.nombrePila}</td>

                <td>${funcionario.unidadAdministrativa}</td>

                <td>${funcionario.cargo}</td>

                <td>${funcionario.estado}</td>

                <td>${funcionario.codigoBiometrico}</td>

                <td>

                    <div class="acciones">

                        <button
                            class="btn btn-warning"
                            onclick="editarFuncionario(${funcionario.id})">

                            Editar

                        </button>

                        <button
                            class="btn btn-danger"
                            onclick="eliminarFuncionario(${funcionario.id})">

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
            "Error al cargar los funcionarios"
        );

    }

}


// ========================================
// ABRIR FORMULARIO
// ========================================

function abrirFormulario() {

    modoEdicion = false;


    document
        .getElementById("funcionarioForm")
        .reset();


    document
        .getElementById("funcionarioId")
        .value = "";


    document
        .getElementById("tituloFormulario")
        .textContent =
        "Nuevo funcionario";


    document
        .getElementById("formularioFuncionario")
        .style.display =
        "block";

}


// ========================================
// CERRAR FORMULARIO
// ========================================

function cerrarFormulario() {

    document
        .getElementById("formularioFuncionario")
        .style.display =
        "none";

}


// ========================================
// CREAR / ACTUALIZAR
// ========================================

document
    .getElementById("funcionarioForm")
    ?.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();


            const id =
                document.getElementById(
                    "funcionarioId"
                ).value;


            const datos = {

                cedula:
                document.getElementById(
                    "cedula"
                ).value,

                nombrePila:
                document.getElementById(
                    "nombrePila"
                ).value,

                unidadAdministrativa:
                document.getElementById(
                    "unidadAdministrativa"
                ).value,

                cargo:
                document.getElementById(
                    "cargo"
                ).value,

                estado:
                document.getElementById(
                    "estado"
                ).value,

                codigoBiometrico:
                document.getElementById(
                    "codigoBiometrico"
                ).value

            };


            try {

                let respuesta;


                // ========================================
                // CREAR
                // ========================================

                if (!modoEdicion) {

                    respuesta =
                        await apiFetch(
                            "/api/funcionarios",
                            {

                                method: "POST",

                                body:
                                    JSON.stringify(datos)

                            }
                        );

                }


                    // ========================================
                    // ACTUALIZAR
                // ========================================

                else {

                    respuesta =
                        await apiFetch(
                            `/api/funcionarios/${id}`,
                            {

                                method: "PUT",

                                body:
                                    JSON.stringify(datos)

                            }
                        );

                }


                if (!respuesta) {
                    return;
                }


                if (!respuesta.ok) {

                    const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el funcionario");
                    alert(mensaje);
                    return;

                }


                if (modoEdicion) {

                    const estado = datos.estado;

                    if (
                        estado === "INACTIVO" ||
                        estado === "JUBILADO" ||
                        estado === "CESADO"
                    ) {

                        alert(
                            "Funcionario dado de baja correctamente.\n\n" +
                            "Las computadoras asignadas han quedado " +
                            "sin funcionario y están disponibles para una " +
                            "nueva asignación."
                        );

                    } else {

                        alert(
                            "Funcionario actualizado correctamente"
                        );
                    }

                } else {

                    alert(
                        "Funcionario creado correctamente"
                    );
                }


                cerrarFormulario();

                cargarFuncionarios();


            } catch (error) {

                console.error(error);

                alert(
                    "Error de conexión con el servidor"
                );

            }

        }
    );


// ========================================
// EDITAR
// ========================================

async function editarFuncionario(id) {

    try {

        const respuesta =
            await apiFetch(
                `/api/funcionarios/${id}`
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "Funcionario no encontrado"
            );

        }


        const funcionario =
            await respuesta.json();


        modoEdicion = true;


        document
            .getElementById("funcionarioId")
            .value =
            funcionario.id;


        document
            .getElementById("cedula")
            .value =
            funcionario.cedula;


        document
            .getElementById("nombrePila")
            .value =
            funcionario.nombrePila;


        document
            .getElementById(
                "unidadAdministrativa"
            )
            .value =
            funcionario.unidadAdministrativa;


        document
            .getElementById("cargo")
            .value =
            funcionario.cargo;


        document
            .getElementById("estado")
            .value =
            funcionario.estado;


        document
            .getElementById(
                "codigoBiometrico"
            )
            .value =
            funcionario.codigoBiometrico;


        document
            .getElementById(
                "tituloFormulario"
            )
            .textContent =
            "Editar funcionario";


        document
            .getElementById(
                "formularioFuncionario"
            )
            .style.display =
            "block";


    } catch (error) {

        console.error(error);

        alert(
            "Error al obtener el funcionario"
        );

    }

}


// ========================================
// ELIMINAR
// ========================================

async function eliminarFuncionario(id) {

    if (!confirm(
        "¿Está seguro de eliminar este funcionario?"
    )) {

        return;

    }


    try {

        const respuesta =
            await apiFetch(
                `/api/funcionarios/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            const mensaje = await obtenerMensajeError(respuesta, "No se pudo eliminar el funcionario");
            alert(mensaje);
            return;

        }


        alert(
            "Funcionario eliminado correctamente"
        );


        cargarFuncionarios();


    } catch (error) {

        console.error(error);

        alert(
            "Error de conexión con el servidor"
        );

    }

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

// ========================================
// INICIO
// ========================================

async function iniciarPagina() {

    await cargarFuncionarios();


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


    await editarFuncionario(Number(idEdicion));

}


iniciarPagina();
