import { Routes } from '@angular/router';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';
import { IndexComponent } from './features/restaurante/index/index.component';
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';
import { LoginComponent } from './features/edge/login.component';           // Asegúrate de importar estos componentes
import { RegisterComponent } from './features/edge/register.component';  // idem

export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' }, // ruta raíz redirecciona a catálogo
  { path: 'registro-restaurante', component: RegistroRestauranteComponent },
  { path: 'restaurante', component: IndexComponent },
  { path: 'cerrar', component: CerrarComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  // Puedes agregar rutas para los dashboards de cliente, restaurante y delivery aquí
  // Ejemplo:
  // { path: 'cliente-dashboard', component: ClienteDashboardComponent },
  // { path: 'restaurante-dashboard', component: RestauranteDashboardComponent },
  // { path: 'delivery-dashboard', component: DeliveryDashboardComponent },

  { path: '**', redirectTo: '' },  // Redirige rutas no conocidas a root
];
