const API_URL = 'http://localhost:8080/api/restaurante';

// Verificar si hay sesión (básico)
const correo = localStorage.getItem('restauranteCorreo');
if (!correo) {
    window.location.href = 'index.html';
}

const modal = document.getElementById('modal');
const cerrarCuentaBtn = document.getElementById('cerrarCuentaBtn');
const confirmarBtn = document.getElementById('confirmarBtn');
const cancelarBtn = document.getElementById('cancelarBtn');
const volverBtn = document.getElementById('volverBtn');
const mensaje = document.getElementById('mensaje');

// Mostrar modal de confirmación
cerrarCuentaBtn.addEventListener('click', () => {
    modal.classList.add('show');
});

// Cancelar cierre
cancelarBtn.addEventListener('click', () => {
    modal.classList.remove('show');
});

// Confirmar cierre de cuenta
confirmarBtn.addEventListener('click', async () => {
    try {
        // Primero obtener el ID del restaurante (simulado, idealmente lo guardas en login)
        const responseGet = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correo, password: 'temp' })
        });
        
        // Para simplificar, asumimos que el ID es 1 (o lo guardas en localStorage)
        const restauranteId = 1; // Cambiar según tu lógica
        
        const response = await fetch(`${API_URL}/${restauranteId}/cerrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ confirm: true })
        });
        
        const data = await response.json();
        
        modal.classList.remove('show');
        
        if (response.ok) {
            mensaje.textContent = data.message;
            mensaje.className = 'mensaje success';
            
            // Limpiar localStorage y redirigir al login
            setTimeout(() => {
                localStorage.removeItem('restauranteCorreo');
                window.location.href = 'index.html';
            }, 2000);
        } else {
            mensaje.textContent = data.message;
            mensaje.className = 'mensaje error';
        }
    } catch (error) {
        modal.classList.remove('show');
        mensaje.textContent = 'Error de conexión con el servidor';
        mensaje.className = 'mensaje error';
    }
});

// Volver al panel (simulado)
volverBtn.addEventListener('click', () => {
    mensaje.textContent = 'Funcionalidad de panel principal pendiente';
    mensaje.className = 'mensaje success';
});
