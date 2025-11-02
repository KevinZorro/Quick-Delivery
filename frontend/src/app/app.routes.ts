import { Routes } from '@angular/router';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';
import { IndexComponent } from './features/restaurante/index/index.component';
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { InicioComponent } from './features/restaurante/Inicio/inicio.component';
import { MenuProductoComponent } from './features/restaurante/menu-producto/menu-producto.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'registro-restaurante', component: RegistroRestauranteComponent },
  { path: 'inicio', component: InicioComponent },
  { path: 'menuProducto', component: MenuProductoComponent },
  { path: 'restaurante', component: IndexComponent },
  { path: 'cerrar', component: CerrarComponent },

  // Puedes agregar rutas para los dashboards de cliente, restaurante y delivery aquí
  // Ejemplo:
  // { path: 'cliente-dashboard', component: ClienteDashboardComponent },
  // { path: 'restaurante-dashboard', component: RestauranteDashboardComponent },
  // { path: 'delivery-dashboard', component: DeliveryDashboardComponent },

  { path: '**', redirectTo: '' },  // Redirige rutas no conocidas a root
];
