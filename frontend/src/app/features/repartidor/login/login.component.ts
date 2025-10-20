import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router'; 
import { AuthService } from '../services/auth.service'; // Asegúrate de que la ruta es correcta

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit(form: NgForm) {
    if (form.valid) {
      const { email, password } = form.value;
      
      this.authService.login({ email, password }).subscribe({
        next: (response) => {
          this.authService.saveTokenAndUser(response.token, response.email);
          alert('¡Bienvenido, Repartidor!');
          // Redirigir a una ruta interna (ej. /repartidor/dashboard)
          this.router.navigate(['/repartidor/dashboard']); 
        },
        error: (err) => {
          console.error('Login fallido:', err);
          alert('Error de autenticación: Credenciales inválidas.');
        }
      });
    }
  }
}