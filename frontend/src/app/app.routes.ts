import { Routes } from '@angular/router';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { MainComponent } from './features/cliente/main.component';
import { DireccionesListaComponent } from './features/cliente/direcciones-lista.component';
import { DireccionFormComponent } from './features/cliente/direccion-form.component';
import { RestauranteMainComponent } from './features/restaurante/main.component';
import { RestauranteDetalleComponent } from './features/cliente/restaurante-detalle.component';
import { DireccionesRestauranteListaComponent } from './features/restaurante/direcciones-restaurante-lista.component';
import { DireccionRestauranteFormComponent } from './features/restaurante/direcciones-restaurante-form.component';
import { ClientePedidosComponent } from './features/cliente/cliente-pedidos.component';
import { DashboardReportesComponent } from './features/reporte/dashboard-reportes.component';
import { DeliveryMainComponent } from './features/delivery/main.component';



export const routes: Routes = [
  // Rutas públicas
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Rutas de restaurante
  { path: 'restaurante/main', component: RestauranteMainComponent },
  { path: 'restaurante/direcciones', component: DireccionesRestauranteListaComponent },
  { path: 'restaurante/direcciones/nueva', component: DireccionRestauranteFormComponent },
  { path: 'restaurante/direcciones/editar/:id', component: DireccionRestauranteFormComponent },
  { path: 'restaurante/direcciones/mapa', component: DireccionFormComponent }, 
  // Dashboard de reportes del restaurante
  { path: 'dashboard-reportes', component: DashboardReportesComponent },
  
  // Rutas de cliente
  { path: 'main', component: MainComponent },
  { path: 'restaurante/:id', component: RestauranteDetalleComponent },
  { path: 'cliente/direcciones', component: DireccionesListaComponent },
  { path: 'cliente/direcciones/nueva', component: DireccionFormComponent },
  { path: 'cliente/direcciones/editar/:id', component: DireccionFormComponent },
  { path: 'cliente/direcciones/mapa', component: DireccionFormComponent },
  { path: 'cliente/pedidos', component: ClientePedidosComponent },

  //Rutas de delivery
{ path: 'delivery/main', component: DeliveryMainComponent },

  
  // Wildcard al FINAL
  { path: '**', redirectTo: 'login' }
];
