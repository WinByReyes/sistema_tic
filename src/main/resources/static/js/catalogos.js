// ========================================
// SESIÓN
// ========================================

const token =
    localStorage.getItem(
        "token"
    );


const rol =
    localStorage.getItem(
        "rol"
    );


if (!token) {

    window.location.href =
        "/login";

}


if (rol !== "ADMIN") {

    alert(
        "No tiene permisos para administrar catálogos."
    );

    window.location.href =
        "/dashboard";

}


// ========================================
// CARGAR CATÁLOGOS
// ========================================

async function cargarCatalogos() {

    const tipo =
        document
            .getElementById(
                "tipoCatalogo"
            )
            .value;


    try {

        const respuesta =
            await apiFetch(
                `/api/catalogos/${tipo}`
            );


        if (!respuesta) {
            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar los catálogos"
            );

        }


        const datos =
            await respuesta.json();


        const tabla =
            document.getElementById(
                "tablaCatalogos"
            );


        tabla.innerHTML =
            "";


        datos.forEach(
            catalogo => {

                const fila =
                    document.createElement(
                        "tr"
                    );


                const estado =
                    catalogo.activo
                        ? "Activo"
                        : "Inactivo";


                fila.innerHTML = `

                    <td>
                        ${catalogo.id}
                    </td>

                    <td>
                        ${escapeHtml(
                    catalogo.nombre
                )}
                    </td>

                    <td>
                        ${estado}
                    </td>

                    <td>

                        <button
                                class="btn btn-warning"
                                onclick="editarCatalogo(
                                    ${catalogo.id},
                                    '${escapeHtml(
                    catalogo.nombre
                )}',
                                    ${catalogo.activo}
                                )">

                            Editar

                        </button>


                        ${
                    catalogo.activo

                        ?

                        `
                                    <button
                                            class="btn btn-danger"
                                            onclick="desactivarCatalogo(
                                                ${catalogo.id}
                                            )">

                                        Desactivar

                                    </button>
                                `

                        :

                        `
                                    <button
                                            class="btn btn-primary"
                                            onclick="activarCatalogo(
                                                ${catalogo.id}
                                            )">

                                        Activar

                                    </button>
                                `
                }

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
            "Error al cargar los catálogos"
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
// ABRIR MODAL
// ========================================

function abrirModalCatalogo() {

    document
        .getElementById(
            "catalogoId"
        )
        .value =
        "";


    document
        .getElementById(
            "nombreCatalogo"
        )
        .value =
        "";


    document
        .getElementById(
            "tituloModal"
        )
        .textContent =
        "Nuevo catálogo";


    document
        .getElementById(
            "modalCatalogo"
        )
        .style.display =
        "flex";

}


// ========================================
// CERRAR MODAL
// ========================================

function cerrarModalCatalogo() {

    document
        .getElementById(
            "modalCatalogo"
        )
        .style.display =
        "none";

}


// ========================================
// GUARDAR
// ========================================

async function guardarCatalogo() {

    const id =
        document
            .getElementById(
                "catalogoId"
            )
            .value;


    const nombre =
        document
            .getElementById(
                "nombreCatalogo"
            )
            .value
            .trim();


    const tipo =
        document
            .getElementById(
                "tipoCatalogo"
            )
            .value;


    if (!nombre) {

        alert(
            "Debe ingresar un nombre."
        );

        return;

    }


    const datos = {

        tipo:
        tipo,

        nombre:
        nombre,

        activo:
            true

    };


    try {

        let respuesta;


        // ====================================
        // ACTUALIZAR
        // ====================================

        if (id) {

            respuesta =
                await apiFetch(
                    `/api/catalogos/${id}`,
                    {

                        method: "PUT",

                        body:
                            JSON.stringify(
                                datos
                            )

                    }
                );

        }


            // ====================================
            // CREAR
        // ====================================

        else {

            respuesta =
                await apiFetch(
                    "/api/catalogos",
                    {

                        method: "POST",

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

            const mensaje = await obtenerMensajeError(respuesta, "No se pudo guardar el catálogo");
            alert(mensaje);
            return;

        }


        alert(

            id

                ? "Catálogo actualizado correctamente."

                : "Catálogo creado correctamente."

        );


        cerrarModalCatalogo();


        await cargarCatalogos();


    } catch (error) {

        console.error(error);

        alert(
            "Error de conexión con el servidor."
        );

    }

}


// ========================================
// EDITAR
// ========================================

function editarCatalogo(
    id,
    nombre,
    activo
) {

    document
        .getElementById(
            "catalogoId"
        )
        .value =
        id;


    document
        .getElementById(
            "nombreCatalogo"
        )
        .value =
        nombre;


    document
        .getElementById(
            "tituloModal"
        )
        .textContent =
        "Editar catálogo";


    document
        .getElementById(
            "modalCatalogo"
        )
        .style.display =
        "flex";

}


// ========================================
// DESACTIVAR
// ========================================

async function desactivarCatalogo(
    id
) {

    if (
        !confirm(
            "¿Desea desactivar este valor?"
        )
    ) {

        return;

    }


    const respuesta =
        await apiFetch(
            `/api/catalogos/${id}/desactivar`,
            {

                method: "PATCH"

            }
        );


    if (
        respuesta &&
        respuesta.ok
    ) {

        alert(
            "Catálogo desactivado."
        );


        await cargarCatalogos();

    }

}


// ========================================
// ACTIVAR
// ========================================

async function activarCatalogo(
    id
) {

    const respuesta =
        await apiFetch(
            `/api/catalogos/${id}/activar`,
            {

                method: "PATCH"

            }
        );


    if (
        respuesta &&
        respuesta.ok
    ) {

        alert(
            "Catálogo activado."
        );


        await cargarCatalogos();

    }

}


// ========================================
// CAMBIO DE CATÁLOGO
// ========================================

const selectorCatalogo =
    document.getElementById(
        "tipoCatalogo"
    );


if (selectorCatalogo) {

    selectorCatalogo.addEventListener(
        "change",
        cargarCatalogos
    );

}


// ========================================
// INICIO
// ========================================

cargarCatalogos();
