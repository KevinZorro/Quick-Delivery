// Archivo: src/app/features/repartidor/repartidor.module.ts

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

// CORRECCIÓN: Eliminar la extensión .ts
import { LoginComponent } from './login/login.component'; 
import { RepartidorRoutingModule } from './repartidor-routing.module'; 


@NgModule({
  declarations: [
    LoginComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RepartidorRoutingModule
  ]
})
export class RepartidorModule { }