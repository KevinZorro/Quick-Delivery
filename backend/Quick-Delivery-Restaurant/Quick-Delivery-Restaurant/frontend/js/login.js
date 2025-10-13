const API_URL = 'http://localhost:8080/api/restaurante';

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const correo = document.getElementById('correo').value;
    const password = document.getElementById('password').value;
    const mensaje = document.getElementById('mensaje');
    
    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correo, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            mensaje.textContent = data.message;
            mensaje.className = 'mensaje success';
            
            // Guardar correo en localStorage
            localStorage.setItem('restauranteCorreo', correo);
            
            // Redirigir a cerrar.html después de 1 segundo
            setTimeout(() => {
                window.location.href = 'cerrar.html';
            }, 1000);
        } else {
            mensaje.textContent = data.message;
            mensaje.className = 'mensaje error';
            
            if (response.status === 423) {
                mensaje.className = 'mensaje warning';
            }
        }
    } catch (error) {
        mensaje.textContent = 'Error de conexión con el servidor';
        mensaje.className = 'mensaje error';
    }
});

// Link de registro (opcional)
document.getElementById('registroLink').addEventListener('click', (e) => {
    e.preventDefault();
    alert('Funcionalidad de registro pendiente');
});
