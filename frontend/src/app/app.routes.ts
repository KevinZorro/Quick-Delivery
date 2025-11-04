import { Routes } from '@angular/router';
import { LoginComponent } from './features/edge/login.component';
import { RegisterComponent } from './features/edge/register.component';
import { MainComponent } from './features/cliente/main.component';
import { DireccionesListaComponent } from './features/cliente/direcciones-lista.component';
import { DireccionFormComponent } from './features/cliente/direccion-form.component';
import { RestauranteMainComponent } from './features/restaurante/main.component';
import { RestauranteDetalleComponent } from './features/cliente/restaurante-detalle.component';
import { DashboardReportesComponent } from './features/reporte/dashboard-reportes.component';
export const routes: Routes = [
  // Rutas públicas
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Ruta principal del restaurante
  {path: 'restaurante/main',component: RestauranteMainComponent},
  // Dashboard de reportes del restaurante
  { path: 'dashboard-reportes', component: DashboardReportesComponent },
  
  // Rutas de cliente
  { path: 'main', component: MainComponent },
  { path: 'restaurante/:id', component: RestauranteDetalleComponent},
  { path: 'cliente/direcciones', component: DireccionesListaComponent },
  { path: 'cliente/direcciones/nueva', component: DireccionFormComponent },
  { path: 'cliente/direcciones/editar/:id', component: DireccionFormComponent },
  
  // Wildcard al FINAL
  { path: '**', redirectTo: 'login' }
];
