export interface PromocionRequest {
  codigo: string;
  descuentoPorcentaje: number;
  fechaExpiracion: string;
  estado: string;
  cantidadUsos: number;
}

export interface PromocionResponse extends PromocionRequest {
  id: string;
}
