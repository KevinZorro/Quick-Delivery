import { Component, OnInit } from '@angular/core';
import { ReporteService } from './reporte.service';
import { ReporteVentas } from './reporte.model';
import { CommonModule } from '@angular/common';  


@Component({
  selector: 'app-dashboard-reportes',
  standalone: true,           
  imports: [CommonModule],
  templateUrl: './dashboard-reportes.component.html'
})
export class DashboardReportesComponent implements OnInit {
  reporte: ReporteVentas | null = null;
  loading: boolean = false;
  errorMessage: string | null = null;

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.cargarReporte();
  }

  cargarReporte(): void {
    this.loading = true;
    this.errorMessage = null;

    this.reporteService.getReporteVentas().subscribe({
      next: (res: ReporteVentas) => {
        this.reporte = res;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error cargando reporte:', err);
        this.errorMessage = 'Error al cargar el reporte';
        this.loading = false;
      }
    });
  }
}
