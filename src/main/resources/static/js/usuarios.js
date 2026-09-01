let modoEdicion = false;


// ================================
// OBTENER JWT
// ================================

const token = localStorage.getItem("token");


// ================================
// VERIFICAR SESIÓN
// ================================

if (!token) {

    window.location.href = "/login";

}


// ================================
// CERRAR SESIÓN
// ================================

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


// ================================
// CARGAR USUARIOS
// ================================

async function cargarUsuarios() {

    try {

        const respuesta =
            await apiFetch("/api/usuarios");


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron obtener los usuarios"
            );

        }


        const usuarios =
            await respuesta.json();


        const tabla =
            document.getElementById(
                "tablaUsuarios"
            );


        tabla.innerHTML = "";


        usuarios.forEach(usuario => {

            const fila =
                document.createElement("tr");


            fila.innerHTML = `

                <td>${usuario.id}</td>

                <td>${usuario.nombre}</td>

                <td>${usuario.usuario}</td>

                <td>${usuario.rol}</td>

                <td>
                    ${usuario.estado
                ? "Activo"
                : "Inactivo"}
                </td>

                <td>

                    <div class="acciones">

                        <button
                            class="btn btn-warning"
                            onclick="editarUsuario(${usuario.id})">

                            Editar

                        </button>

                        <button
                            class="btn btn-danger"
                            onclick="eliminarUsuario(${usuario.id})">

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
            "Error al cargar los usuarios"
        );

    }

}


// ================================
// ABRIR FORMULARIO
// ================================

function abrirFormulario() {

    modoEdicion = false;


    document.getElementById(
        "usuarioForm"
    ).reset();


    document.getElementById(
        "usuarioId"
    ).value = "";


    document.getElementById(
        "tituloFormulario"
    ).textContent =
        "Nuevo usuario";


    document.getElementById(
        "formularioUsuario"
    ).style.display =
        "block";

}


// ================================
// CERRAR FORMULARIO
// ================================

function cerrarFormulario() {

    document.getElementById(
        "formularioUsuario"
    ).style.display =
        "none";

}


// ================================
// GUARDAR USUARIO
// ================================

document.getElementById("usuarioForm")
    ?.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();


            const id =
                document.getElementById(
                    "usuarioId"
                ).value;


            const datos = {

                nombre:
                document.getElementById(
                    "nombre"
                ).value,

                usuario:
                document.getElementById(
                    "usuario"
                ).value,

                contrasena:
                document.getElementById(
                    "contrasena"
                ).value,

                rol:
                document.getElementById(
                    "rol"
                ).value,

                estado:
                    document.getElementById(
                        "estado"
                    ).value === "true"

            };


            try {

                let respuesta;


                // ================================
                // CREAR
                // ================================

                if (!modoEdicion) {

                    respuesta =
                        await apiFetch(
                            "/api/usuarios",
                            {

                                method: "POST",

                                body:
                                    JSON.stringify(datos)

                            }
                        );

                }


                    // ================================
                    // ACTUALIZAR
                // ================================

                else {

                    respuesta =
                        await apiFetch(
                            `/api/usuarios/${id}`,
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

                    const errorMsg = await obtenerMensajeError(respuesta, "No se pudo guardar el usuario");
                    alert(errorMsg);
                    return;

                }


                alert(

                    modoEdicion

                        ? "Usuario actualizado correctamente"

                        : "Usuario creado correctamente"

                );


                cerrarFormulario();

                cargarUsuarios();

            }


            catch (error) {

                console.error(error);

                alert(
                    "Error de conexión con el servidor"
                );

            }

        }
    );


// ================================
// EDITAR USUARIO
// ================================

async function editarUsuario(id) {

    try {

        const respuesta =
            await apiFetch(
                `/api/usuarios/${id}`
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se encontró el usuario"
            );

        }


        const usuario =
            await respuesta.json();


        modoEdicion = true;


        document.getElementById(
            "usuarioId"
        ).value =
            usuario.id;


        document.getElementById(
            "nombre"
        ).value =
            usuario.nombre;


        document.getElementById(
            "usuario"
        ).value =
            usuario.usuario;


        document.getElementById(
            "contrasena"
        ).value =
            "";


        document.getElementById(
            "rol"
        ).value =
            usuario.rol;


        document.getElementById(
            "estado"
        ).value =
            usuario.estado
                ? "true"
                : "false";


        document.getElementById(
            "tituloFormulario"
        ).textContent =
            "Editar usuario";


        document.getElementById(
            "formularioUsuario"
        ).style.display =
            "block";

    }


    catch (error) {

        console.error(error);

        alert(
            "Error al obtener el usuario"
        );

    }

}


// ================================
// ELIMINAR USUARIO
// ================================

async function eliminarUsuario(id) {

    const confirmar =
        confirm(
            "¿Está seguro de eliminar este usuario?"
        );


    if (!confirmar) {

        return;

    }


    try {

        const respuesta =
            await apiFetch(
                `/api/usuarios/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            const errorMsg = await obtenerMensajeError(respuesta, "No se pudo eliminar el usuario");
            alert(errorMsg);
            return;

        }


        alert(
            "Usuario eliminado correctamente"
        );


        cargarUsuarios();

    }


    catch (error) {

        console.error(error);

        alert(
            "Error al eliminar el usuario"
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

// ================================
// INICIO
// ================================

cargarUsuarios();
