const API_URL = 'http://localhost:8081/api/restaurante';

// Obtener datos del localStorage
const correo = localStorage.getItem('restauranteCorreo');
const restauranteId = localStorage.getItem('restauranteId');

// Debug: ver qué hay en localStorage
console.log('=== VALORES EN LOCALSTORAGE ===');
console.log('Correo:', correo);
console.log('ID:', restauranteId);
console.log('================================');

// Validar que el usuario haya iniciado sesión
// if (!correo || !restauranteId) {
//     alert('Debes iniciar sesión primeroooooooo');
//     localStorage.clear(); // Limpiar todo
//     window.location.href = 'index.html';
//     throw new Error('Redirigiendo al login...'); // Detener ejecución
// }

// Elementos del DOM
const modal = document.getElementById('modal');
const cerrarCuentaBtn = document.getElementById('cerrarCuentaBtn');
const confirmarBtn = document.getElementById('confirmarBtn');
const cancelarBtn = document.getElementById('cancelarBtn');
const volverBtn = document.getElementById('volverBtn');
const mensaje = document.getElementById('mensaje');

// Abrir modal
cerrarCuentaBtn.addEventListener('click', () => {
    modal.classList.add('show');
});

// Cerrar modal
cancelarBtn.addEventListener('click', () => {
    modal.classList.remove('show');
});

// Confirmar cierre de cuenta
confirmarBtn.addEventListener('click', async () => {
    try {
        console.log('Intentando cerrar cuenta con ID:', restauranteId);

        const response = await fetch(`${API_URL}/${restauranteId}/cerrar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ confirm: true })
        });

        const data = await response.json();
        modal.classList.remove('show');

        console.log('Respuesta del servidor:', data);

        if (response.ok) {
            mensaje.textContent = data.message || 'Cuenta cerrada correctamente';
            mensaje.className = 'mensaje success';

            setTimeout(() => {
                localStorage.clear();
                window.location.href = 'index.html';
            }, 2000);
        } else {
            mensaje.textContent = data.message || 'Error al cerrar cuenta';
            mensaje.className = 'mensaje error';
        }
    } catch (error) {
        console.error('Error al cerrar cuenta:', error);
        modal.classList.remove('show');
        mensaje.textContent = 'Error de conexión con el servidor';
        mensaje.className = 'mensaje error';
    }
});

// Botón volver
if (volverBtn) {
    volverBtn.addEventListener('click', () => {
        window.location.href = 'home.html';
    });
}
