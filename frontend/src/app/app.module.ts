import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { IndexComponent } from './features/restaurante/index/index.component';
// importa otros componentes si es necesario...

@NgModule({
  declarations: [
    IndexComponent,
    // otros componentes...
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    IndexComponent
    // otros módulos...
  ],
  bootstrap: [IndexComponent] // o tu componente raíz principal
})
export class AppModule { }
