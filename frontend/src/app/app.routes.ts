import { Routes } from '@angular/router';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { InicioComponent } from './features/restaurante/inicio/inicio.component';
import { MenuProductoComponent } from './features/restaurante/menu-producto/menu-producto.component';
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';
import { IndexComponent } from './features/restaurante/index/index.component';
import { MainComponent } from './features/cliente/main.component';
import { RestauranteDetalleComponent } from './features/cliente/restaurante-detalle.component';
import { DireccionesListaComponent } from './features/cliente/direcciones-lista.component';
import { DireccionFormComponent } from './features/cliente/direccion-form.component';

export const routes: Routes = [
  // Rutas públicas
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Rutas de restaurante (administrador)
  { path: 'inicio', component: InicioComponent },
  { path: 'menuProducto', component: MenuProductoComponent },
  { path: 'registro-restaurante', component: RegistroRestauranteComponent },
  { path: 'restaurante-index', component: IndexComponent }, // ← RENOMBRADA para evitar conflicto
  { path: 'cerrar', component: CerrarComponent },
  
  // Rutas de cliente
  { path: 'main', component: MainComponent },
  { path: 'cliente/direcciones', component: DireccionesListaComponent },
  { path: 'cliente/direcciones/nueva', component: DireccionFormComponent },
  { path: 'cliente/direcciones/editar/:id', component: DireccionFormComponent },
  
  // Wildcard al FINAL
  { path: '**', redirectTo: 'login' }
];
