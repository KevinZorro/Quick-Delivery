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
import { DeliveryEntregasComponent } from './features/delivery/entregas.component';
import { RecuperarContrasenaComponent } from './features/edge/recuperar-contrasena.component';
import { ResetPasswordComponent } from './features/edge/reset-password.component';
import { ClientePerfilComponent } from './features/cliente/perfil.component';
import { RestaurantePerfilComponent } from './features/restaurante/perfil.component';
import { DeliveryPerfilComponent } from './features/delivery/perfil.component';
export const routes: Routes = [
  // Rutas públicas
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'recuperar-contrasena', component: RecuperarContrasenaComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  
  // Rutas de restaurante
  { path: 'restaurante/main', component: RestauranteMainComponent },
  { path: 'restaurante/perfil', component: RestaurantePerfilComponent },
  {
    path: 'restaurante/direcciones',
    component: DireccionesRestauranteListaComponent,
  },
  {
    path: 'restaurante/direcciones/nueva',
    component: DireccionRestauranteFormComponent,
  },
  {
    path: 'restaurante/direcciones/editar/:id',
    component: DireccionRestauranteFormComponent,
  },
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
  { path: 'perfil', component: ClientePerfilComponent },

  // Rutas de delivery
  { path: 'delivery/main', component: DeliveryMainComponent },
  { path: 'delivery/entregas', component: DeliveryEntregasComponent },
  { path: 'delivery/perfil', component: DeliveryPerfilComponent },

  // Wildcard al FINAL
  { path: '**', redirectTo: 'login' },
];
