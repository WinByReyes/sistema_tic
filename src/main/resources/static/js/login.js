const formulario = document.getElementById("loginForm");

formulario.addEventListener("submit", async function (event) {

    event.preventDefault();

    const usuario = document.getElementById("usuario").value.trim();
    const contrasena = document.getElementById("contrasena").value;

    const mensaje = document.getElementById("mensaje");
    const boton = formulario.querySelector('button[type="submit"]');

    if (!usuario || !contrasena) {
        mensaje.textContent = "Ingrese su usuario y contraseña.";
        return;
    }

    mensaje.textContent = "Iniciando sesión...";

    boton.disabled = true;

    try {

        const respuesta = await apiFetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                usuario: usuario,
                contrasena: contrasena
            })
        });

        if (!respuesta.ok) {
            mensaje.textContent = await obtenerMensajeError(
                respuesta,
                "Usuario o contraseña incorrectos."
            );
            return;
        }

        const datos = await respuesta.json();

        // Guardamos el JWT
        localStorage.setItem("token", datos.token);

        // Opcional: guardar información del usuario
        localStorage.setItem("usuario", datos.usuario);
        localStorage.setItem("nombre", datos.nombre);
        localStorage.setItem("rol", datos.rol);

        mensaje.textContent = "Inicio de sesión exitoso.";

        // Ir al dashboard
        window.location.href = "/dashboard";

    } catch (error) {

        console.error("Error al iniciar sesión:", error);
        mensaje.textContent = error.message || "No se pudo conectar con el servidor.";
    } finally {
        boton.disabled = false;
    }

});
