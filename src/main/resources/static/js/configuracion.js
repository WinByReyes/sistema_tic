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
        "No tiene permisos para editar la configuración institucional."
    );

    window.location.href =
        "/dashboard";

}


// ========================================
// CARGAR CONFIGURACIÓN
// ========================================

async function cargarConfiguracion() {

    const respuesta =
        await apiFetch(
            "/api/configuracion-institucional"
        );

    if (!respuesta) {
        return;
    }

    if (!respuesta.ok) {

        alert(
            "No se pudo cargar la configuración institucional."
        );

        return;
    }


    const configuracion =
        await respuesta.json();


    document.getElementById("configuracionId").value =
        configuracion.id ?? "";

    document.getElementById("nombreInstitucion").value =
        configuracion.nombreInstitucion ?? "";

    document.getElementById("unidadAdministrativa").value =
        configuracion.unidadAdministrativa ?? "";

    document.getElementById("direccion").value =
        configuracion.direccion ?? "";

    document.getElementById("telefono").value =
        configuracion.telefono ?? "";

    document.getElementById("correo").value =
        configuracion.correo ?? "";

}


// ========================================
// GUARDAR CONFIGURACIÓN
// ========================================

document
    .getElementById("configuracionForm")
    ?.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const datos = {

                nombreInstitucion:
                    document.getElementById(
                        "nombreInstitucion"
                    ).value,

                unidadAdministrativa:
                    document.getElementById(
                        "unidadAdministrativa"
                    ).value,

                direccion:
                    document.getElementById(
                        "direccion"
                    ).value,

                telefono:
                    document.getElementById(
                        "telefono"
                    ).value,

                correo:
                    document.getElementById(
                        "correo"
                    ).value

            };


            const respuesta =
                await apiFetch(
                    "/api/configuracion-institucional",
                    {
                        method: "PUT",
                        body: JSON.stringify(datos)
                    }
                );

            if (!respuesta) {
                return;
            }


            if (respuesta.ok) {

                alert(
                    "Configuración institucional actualizada correctamente."
                );

                await cargarConfiguracion();

            } else {

                const errorMsg = await obtenerMensajeError(respuesta, "No se pudo actualizar la configuración institucional.");
                alert(errorMsg);
            }
        }
    );


// ========================================
// INICIO
// ========================================

cargarConfiguracion();
