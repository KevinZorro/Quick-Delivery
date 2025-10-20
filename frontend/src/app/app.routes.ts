import { Routes } from '@angular/router';
import { ProductCatalogComponent } from './features/product-catalog/product-catalog.component';

export const routes: Routes = [
  { path: '', component: ProductCatalogComponent },  // Ruta principal
  { path: 'catalogo', component: ProductCatalogComponent }
  {
    path: 'repartidor', // La URL para el acceso del repartidor
    // Carga perezosa del módulo Repartidor
    loadChildren: () => import('./features/repartidor/repartidor.module').then(m => m.RepartidorModule)
  }
];
