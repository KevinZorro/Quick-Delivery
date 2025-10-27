import { Routes } from '@angular/router';
import { ProductCatalogComponent } from './features/cliente/product-catalog/product-catalog.component';
import { RegistroRestauranteComponent } from './features/restaurante/features/restaurante/registro-restaurante/registro-restaurante.component';

export const routes: Routes = [
  { path: '', component: ProductCatalogComponent },  // Ruta principal
  { path: 'catalogo', component: ProductCatalogComponent },
  { path: 'registro-restaurante', component: RegistroRestauranteComponent }  // Nueva ruta
import { IndexComponent } from './features/restaurante/index/index.component';
import { CerrarComponent } from './features/restaurante/cerrar/cerrar.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },  // Ruta principal restaurante
  { path: 'cerrar', component: CerrarComponent }
];
