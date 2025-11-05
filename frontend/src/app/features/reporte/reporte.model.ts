export interface PlatosVendidos {
    cantidadVendida: number;
    productoId: string;
    categoria: string;
    nombre: string;
  }
  
  export interface EstadoPedidos {
    completados: number;
    enCurso: number;
  }
  
  export interface ReporteVentas {
    totalPedidos: number;
    platosMasVendidos: PlatosVendidos[];
    estadoPedidos: EstadoPedidos;
    ingresosPorCategoria: Record<string, number>;
    top10Productos: PlatosVendidos[];
    productosBajasVentas: PlatosVendidos[];
    ingresosTotales: number;
  }
  