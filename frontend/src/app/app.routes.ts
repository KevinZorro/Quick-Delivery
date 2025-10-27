import { Routes } from '@angular/router';
import { IndexComponent } from './features/restaurante/index/index.component';
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },  // Ruta principal restaurante
  { path: 'cerrar', component: CerrarComponent }
];
