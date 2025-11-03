import { Routes } from '@angular/router';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { InicioComponent } from './features/restaurante/Inicio/inicio.component'; // ← VERIFICA ESTA RUTA
import { MenuProductoComponent } from './features/restaurante/menu-producto/menu-producto.component'; // ← VERIFICA ESTA RUTA
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';
import { IndexComponent } from './features/restaurante/index/index.component';

export const routes: Routes = [
  // Rutas públicas
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Ruta principal del restaurante
  { path: 'restaurante/main', component: RestauranteMainComponent },
  
  // Rutas de cliente
  { path: 'main', component: MainComponent },
  { path: 'cliente/direcciones', component: DireccionesListaComponent },
  { path: 'cliente/direcciones/nueva', component: DireccionFormComponent },
  { path: 'cliente/direcciones/editar/:id', component: DireccionFormComponent },
  
  // Wildcard al FINAL
  { path: '**', redirectTo: 'login' }
];
