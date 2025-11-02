import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { importProvidersFrom } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { LoginComponent } from './app/features/edge/login.component';
import { RegisterComponent } from './app/features/edge/register.component';
import { MainComponent } from './app/features/cliente/main.component';
import { RestauranteDetalleComponent } from './app/features/cliente/restaurante-detalle.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' as const },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'main', component: MainComponent },
  { path: 'restaurante/:id', component: RestauranteDetalleComponent },
  // Agrega aquí las rutas para restaurante-dashboard y delivery-dashboard cuando las tengas
];

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(RouterModule.forRoot(routes)),
    provideHttpClient(),
  ]
}).catch(err => console.error(err));
