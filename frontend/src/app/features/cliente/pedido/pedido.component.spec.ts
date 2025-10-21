import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PedidoComponent } from './pedido.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('PedidoComponent', () => {
  let component: PedidoComponent;
  let fixture: ComponentFixture<PedidoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoComponent, ReactiveFormsModule, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidoComponent);
    component = fixture.componentInstance;
    component.producto = {
      id: '123',
      nombre: 'Producto Test',
      precio: 100
    };
    fixture.detectChanges();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería calcular el subtotal correctamente', () => {
    component.pedidoForm.patchValue({ cantidad: 2 });
    expect(component.subtotal).toBe(200);
  });
});
