const API_URL = 'http://localhost:8081/api/restaurante';

document.addEventListener('DOMContentLoaded', () => {
    // Limpiar localStorage al entrar al login
    localStorage.clear();
    console.log('✅ LocalStorage limpiado');

    const loginForm = document.getElementById('loginForm');
    const correoInput = document.getElementById('correo');
    const passwordInput = document.getElementById('password');
    const mensajeLogin = document.getElementById('mensajeLogin');

    if (!loginForm) {
        console.error('❌ No se encontró el formulario de login');
        return;
    }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const correo = correoInput.value.trim();
        const password = passwordInput.value.trim();

        console.log('🔄 === INTENTANDO LOGIN ===');
        console.log('📧 Correo:', correo);
        console.log('==========================');

        if (!correo || !password) {
            mostrarMensaje('Por favor completa todos los campos', 'error');
            return;
        }

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ correo, password })
            });

            const data = await response.json();

            console.log('📥 === RESPUESTA DEL SERVIDOR ===');
            console.log('Status:', response.status);
            console.log('Message:', data.message);
            console.log('UserId:', data.userId);
            console.log('==================================');

            if (response.ok && data.userId) {
                // ✅ GUARDAR EN LOCALSTORAGE
                localStorage.setItem('restauranteCorreo', correo);
                localStorage.setItem('restauranteId', data.userId);
                
                console.log('💾 === VALORES GUARDADOS ===');
                console.log('Correo:', localStorage.getItem('restauranteCorreo'));
                console.log('ID:', localStorage.getItem('restauranteId'));
                console.log('============================');
                
                mostrarMensaje('Login exitoso. Redirigiendo...', 'success');

                // Redirigir después de 1 segundo
                setTimeout(() => {
                    console.log('🔀 Redirigiendo a cerrar.html...');
                    window.location.href = 'cerrar.html';
                }, 1000);
            } else {
                mostrarMensaje(data.message || 'Credenciales incorrectas', 'error');
            }
        } catch (error) {
            console.error('❌ === ERROR EN LOGIN ===');
            console.error(error);
            console.error('=========================');
            mostrarMensaje('Error de conexión con el servidor', 'error');
        }
    });

    function mostrarMensaje(texto, tipo) {
        if (mensajeLogin) {
            mensajeLogin.textContent = texto;
            mensajeLogin.className = `mensaje ${tipo}`;
            mensajeLogin.style.display = 'block';
        } else {
            alert(texto);
        }
    }
});
