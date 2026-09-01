/*
 * =========================================================
 * DASHBOARD
 * Sistema TIC
 *
 * Este archivo solamente consulta información del backend.
 * =========================================================
 */

// =========================================================
// JWT
// =========================================================

const token = localStorage.getItem("token");
const userRole = localStorage.getItem("rol");

if (!token) {
    window.location.href = "/login";
}


// =========================================================
// INFORMACIÓN DEL USUARIO
// =========================================================

function cargarInformacionUsuario() {

    const nombre =
        localStorage.getItem("nombre") ||
        localStorage.getItem("usuario") ||
        "Usuario";


    const usuario =
        localStorage.getItem("usuario") ||
        "Usuario";


    const rol =
        userRole ||
        "Usuario";


    /*
     * Nombre del usuario
     */

    const nombreUsuario =
        document.getElementById(
            "nombreUsuario"
        );


    if (nombreUsuario) {

        nombreUsuario.textContent =
            nombre;

    }


    /*
     * Rol en la barra superior
     */

    const rolUsuario =
        document.getElementById(
            "rolUsuario"
        );


    if (rolUsuario) {

        rolUsuario.textContent =
            rol;

    }


    /*
     * Perfil del lado derecho
     */

    const profileNombre =
        document.getElementById(
            "profileNombre"
        );


    if (profileNombre) {

        profileNombre.textContent =
            nombre;

    }


    const profileRol =
        document.getElementById(
            "profileRol"
        );


    if (profileRol) {

        profileRol.textContent =
            rol;

    }


    /*
     * Inicial del usuario
     */

    const inicial =
        nombre
            .trim()
            .charAt(0)
            .toUpperCase();


    const avatarUsuario =
        document.getElementById(
            "avatarUsuario"
        );


    if (avatarUsuario) {

        avatarUsuario.textContent =
            inicial || "U";

    }


    const profileAvatar =
        document.getElementById(
            "profileAvatar"
        );


    if (profileAvatar) {

        profileAvatar.textContent =
            inicial || "U";

    }

}


// =========================================================
// MOSTRAR NÚMERO
// =========================================================

function mostrarNumero(id, numero) {

    const elemento =
        document.getElementById(id);


    if (!elemento) {
        return;
    }


    elemento.textContent =
        numero;

}


// =========================================================
// CARGAR USUARIOS
// =========================================================

async function cargarTotalUsuarios() {

    try {

        const respuesta =
            await apiFetch(
                "/api/usuarios"
            );


        if (!respuesta) {
            return;
        }


        /*
         * Un TECNICO no tiene permiso para
         * consultar usuarios según el backend.
         */

        if (respuesta.status === 403) {

            mostrarNumero(
                "totalUsuarios",
                "—"
            );

            return;

        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron obtener los usuarios"
            );

        }


        const usuarios =
            await respuesta.json();


        mostrarNumero(
            "totalUsuarios",
            usuarios.length
        );


    } catch (error) {

        console.error(
            "Error usuarios:",
            error
        );


        mostrarNumero(
            "totalUsuarios",
            "—"
        );

    }

}


// =========================================================
// CARGAR FUNCIONARIOS
// =========================================================

async function cargarTotalFuncionarios() {

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
                "No se pudieron obtener los funcionarios"
            );

        }


        const funcionarios =
            await respuesta.json();


        mostrarNumero(
            "totalFuncionarios",
            funcionarios.length
        );


    } catch (error) {

        console.error(
            "Error funcionarios:",
            error
        );


        mostrarNumero(
            "totalFuncionarios",
            "—"
        );

    }

}


// =========================================================
// CARGAR COMPUTADORAS
// =========================================================

async function cargarTotalComputadoras() {

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
                "No se pudieron obtener las computadoras"
            );

        }


        const computadoras =
            await respuesta.json();


        mostrarNumero(
            "totalComputadoras",
            computadoras.length
        );


    } catch (error) {

        console.error(
            "Error computadoras:",
            error
        );


        mostrarNumero(
            "totalComputadoras",
            "—"
        );

    }

}


// =========================================================
// CARGAR MANTENIMIENTOS
// =========================================================

async function cargarTotalMantenimientos() {

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
                "No se pudieron obtener los mantenimientos"
            );

        }


        const mantenimientos =
            await respuesta.json();


        mostrarNumero(
            "totalMantenimientos",
            mantenimientos.length
        );


    } catch (error) {

        console.error(
            "Error mantenimientos:",
            error
        );


        mostrarNumero(
            "totalMantenimientos",
            "—"
        );

    }

}


// =========================================================
// ÚLTIMAS CONSULTAS
// =========================================================

function cargarUltimasConsultas() {

    const contenedor =
        document.getElementById(
            "ultimasConsultas"
        );


    if (!contenedor) {
        return;
    }


    const consultas =
        JSON.parse(
            localStorage.getItem(
                "ultimasConsultas"
            ) || "[]"
        );


    if (
        !consultas ||
        consultas.length === 0
    ) {

        contenedor.innerHTML = `

            <div class="empty-recent">

                <span>⌕</span>

                <p>
                    Todavía no se han realizado consultas.
                </p>

            </div>

        `;

        return;

    }


    contenedor.innerHTML = "";


    consultas
        .slice(0, 5)
        .forEach(consulta => {

            const elemento =
                document.createElement("div");


            elemento.className =
                "recent-item";


            elemento.innerHTML = `

                <div class="recent-icon">
                    ${consulta.icono || "⌕"}
                </div>

                <div class="recent-information">

                    <strong>
                        ${consulta.nombre}
                    </strong>

                    <span>
                        ${consulta.tipo}
                    </span>

                </div>

                <div class="recent-time">
                    ${consulta.hora}
                </div>

            `;


            contenedor.appendChild(
                elemento
            );

        });

}


// =========================================================
// CERRAR SESIÓN
// =========================================================

function cerrarSesion() {

    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    localStorage.removeItem("nombre");
    localStorage.removeItem("rol");
    localStorage.removeItem("idUsuario");

    window.location.href =
        "/login";

}


// =========================================================
// BOTÓN LOGOUT
// =========================================================

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


// =========================================================
// MENÚ SEGÚN ROL
// =========================================================

const menuAdministracion =
    document.getElementById(
        "menuAdministracion"
    );

if (
    menuAdministracion &&
    userRole !== "ADMIN"
) {

    menuAdministracion.style.display =
        "none";
}


// =========================================================
// CARGAR DASHBOARD
// =========================================================

async function cargarDashboard() {

    cargarInformacionUsuario();

    await Promise.all([

        cargarTotalUsuarios(),

        cargarTotalFuncionarios(),

        cargarTotalComputadoras(),

        cargarTotalMantenimientos()

    ]);

    cargarUltimasConsultas();

}

// =========================================================
// INICIAR
// =========================================================

cargarDashboard();
