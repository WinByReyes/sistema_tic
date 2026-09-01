/* Utilidades comunes para las llamadas al API del Sistema TIC. */

async function apiFetch(url, opciones = {}) {
    const token = localStorage.getItem("token");
    const esFormulario = opciones.body instanceof FormData;
    const headers = {
        ...(opciones.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };

    if (!esFormulario && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    let respuesta;
    try {
        respuesta = await fetch(url, { ...opciones, headers });
    } catch (causa) {
        const error = new Error("No se pudo conectar con el servidor. Verifique su conexión e intente nuevamente.");
        error.cause = causa;
        error.codigo = "SIN_CONEXION";
        throw error;
    }

    // Manejo de 401: Sesión expirada o no autorizada
    if (respuesta.status === 401 && !url.startsWith("/api/auth/")) {
        localStorage.removeItem("token");
        localStorage.removeItem("rol");
        localStorage.removeItem("usuario");
        localStorage.removeItem("nombre");
        if (window.location.pathname !== "/login") {
            window.location.replace("/login");
        }
        return null;
    }

    return respuesta;
}

// Alias global centralizado para todo el proyecto
const fetchConToken = apiFetch;

// Asignación explícita a window para compatibilidad global
if (typeof window !== "undefined") {
    window.apiFetch = apiFetch;
    window.fetchConToken = fetchConToken;
}

async function obtenerMensajeError(respuesta, mensajePorDefecto = "No fue posible completar la operación.") {
    if (!respuesta) {
        return mensajePorDefecto;
    }
    try {
        const error = await respuesta.clone().json();
        if (error.errores && typeof error.errores === "object") {
            const lista = Object.values(error.errores).filter(Boolean);
            if (lista.length > 0) {
                return lista.join(". ");
            }
        }
        return error.mensaje || error.error || error.message || mensajePorDefecto;
    } catch (_) {
        try {
            const texto = await respuesta.clone().text();
            return texto && texto.length < 200 ? texto : mensajePorDefecto;
        } catch (__) {
            return mensajePorDefecto;
        }
    }
}

if (typeof window !== "undefined") {
    window.obtenerMensajeError = obtenerMensajeError;
}
