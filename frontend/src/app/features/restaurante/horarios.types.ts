export interface HorarioAtencion {
  id: string;
  restauranteId: string;
  diaSemana: string;   // LUNES, MARTES...
  horaApertura: string; // "09:00"
  horaCierre: string;   // "18:00"
  activo: boolean;
}

export interface HorarioAtencionDto {
  diaSemana: string;
  horaApertura: string;
  horaCierre: string;
  activo: boolean;
}

export interface InterrupcionEspecial {
  id: string;
  restauranteId: string;
  fecha: string; // YYYY-MM-DD
  motivo: string;
}
