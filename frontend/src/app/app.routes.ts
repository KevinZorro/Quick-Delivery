import { Routes } from '@angular/router';
import { ProductCatalogComponent } from './features/product-catalog/product-catalog.component';

export const routes: Routes = [
  { path: '', component: ProductCatalogComponent },  // Ruta principal
  { path: 'catalogo', component: ProductCatalogComponent }
];
