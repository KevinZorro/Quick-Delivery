import { Routes } from '@angular/router';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { InicioComponent } from './features/restaurante/inicio/inicio.component'; // ← VERIFICA ESTA RUTA
import { MenuProductoComponent } from './features/restaurante/menu-producto/menu-producto.component'; // ← VERIFICA ESTA RUTA
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';
import { IndexComponent } from './features/restaurante/index/index.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio', component: InicioComponent },
  { path: 'menuProducto', component: MenuProductoComponent },
  { path: 'registro-restaurante', component: RegistroRestauranteComponent },
  { path: 'restaurante', component: IndexComponent },
  { path: 'cerrar', component: CerrarComponent },
  { path: '**', redirectTo: 'login' }
];
