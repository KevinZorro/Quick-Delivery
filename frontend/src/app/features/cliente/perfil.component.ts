import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { HeaderComponent } from './header.component';
import { EliminarCuentaComponent } from '../shared/eliminar-cuenta.component';

@Component({
  selector: 'app-cliente-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, EliminarCuentaComponent],
  templateUrl: './perfil.component.html',
})
export class ClientePerfilComponent implements OnInit {
  userName: string = '';
  userEmail: string = '';
  userTelefono: string = '';
  userRole: string = '';
  fotoPerfil: string | null = null;
  nuevaFotoUrl: string = '';
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  private platformId = inject(PLATFORM_ID);

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      this.cargarPerfil();
    }
  }

  cargarPerfil(): void {
    this.loading = true;
    this.errorMessage = null;

    this.authService.obtenerMiPerfil().subscribe({
      next: (perfil) => {
        this.userName = perfil.nombre;
        this.userEmail = perfil.correo;
        this.userTelefono = perfil.telefono;
        this.userRole = perfil.rol;
        this.fotoPerfil = perfil.fotoPerfil;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error al cargar el perfil';
        console.error(err);
        // Fallback a datos del localStorage si falla
        this.userName = this.authService.getUserName() || 'Usuario';
        this.userEmail = localStorage.getItem('quick-delivery-userEmail') || '';
        this.userRole = this.authService.getUserRole() || '';
      },
    });
  }

  actualizarFotoPerfil(): void {
    if (!this.nuevaFotoUrl || this.nuevaFotoUrl.trim() === '') {
      this.errorMessage = 'Por favor ingresa una URL válida';
      return;
    }

    // Validación básica de URL de imagen
    const urlLower = this.nuevaFotoUrl.toLowerCase();
    const esImagenValida =
      urlLower.endsWith('.jpg') ||
      urlLower.endsWith('.jpeg') ||
      urlLower.endsWith('.png') ||
      urlLower.endsWith('.gif') ||
      urlLower.endsWith('.webp') ||
      urlLower.endsWith('.svg') ||
      urlLower.includes('.jpg?') ||
      urlLower.includes('.jpeg?') ||
      urlLower.includes('.png?') ||
      urlLower.includes('.gif?') ||
      urlLower.includes('.webp?') ||
      urlLower.includes('.svg?') ||
      this.nuevaFotoUrl.startsWith('data:image/') ||
      // Aceptar URLs de servicios conocidos de imágenes
      urlLower.includes('gstatic.com/images') ||
      urlLower.includes('gstatic.com/image') ||
      urlLower.includes('imgur.com') ||
      urlLower.includes('cloudinary.com') ||
      urlLower.includes('unsplash.com') ||
      urlLower.includes('pexels.com') ||
      urlLower.includes('pixabay.com') ||
      urlLower.includes('images.unsplash.com') ||
      urlLower.includes('cdn.') ||
      urlLower.includes('/image/') ||
      urlLower.includes('/images/') ||
      urlLower.includes('/img/') ||
      // URLs HTTP/HTTPS válidas con indicadores de imagen
      ((urlLower.startsWith('http://') || urlLower.startsWith('https://')) &&
       (urlLower.includes('image') || urlLower.includes('img') || urlLower.includes('photo') || urlLower.includes('picture')));

    if (!esImagenValida) {
      this.errorMessage = 'La URL debe ser una imagen válida (.jpg, .jpeg, .png, .gif, .webp o base64)';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.authService.actualizarFotoPerfil(this.nuevaFotoUrl).subscribe({
      next: (perfil) => {
        this.fotoPerfil = perfil.fotoPerfil;
        this.nuevaFotoUrl = '';
        this.loading = false;
        this.successMessage = 'Foto de perfil actualizada exitosamente';
        setTimeout(() => {
          this.successMessage = null;
        }, 3000);
      },
      error: (err) => {
        this.loading = false;
        if (err.error && typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = 'Error al actualizar la foto de perfil';
        }
      },
    });
  }

  eliminarFotoPerfil(): void {
    const confirmacion = confirm('¿Estás seguro de que deseas eliminar tu foto de perfil?');
    if (!confirmacion) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.authService.actualizarFotoPerfil(null).subscribe({
      next: (perfil) => {
        this.fotoPerfil = perfil.fotoPerfil;
        this.loading = false;
        this.successMessage = 'Foto de perfil eliminada exitosamente';
        setTimeout(() => {
          this.successMessage = null;
        }, 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error al eliminar la foto de perfil';
      },
    });
  }
}
