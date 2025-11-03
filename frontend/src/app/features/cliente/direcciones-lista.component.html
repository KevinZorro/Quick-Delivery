<app-header></app-header>

<div class="container mx-auto px-4 py-8">
  <div class="flex justify-between items-center mb-6">
    <h2 class="text-3xl font-bold text-gray-800">Mis Direcciones</h2>
    <button 
      (click)="agregarDireccion()"
      class="bg-teal-600 hover:bg-teal-700 text-white px-6 py-2 rounded-lg transition">
      + Agregar Dirección
    </button>
  </div>

  <!-- Loading -->
  <div *ngIf="loading" class="text-center py-8">
    <p class="text-gray-600">Cargando direcciones...</p>
  </div>

  <!-- Error -->
  <div *ngIf="errorMessage && !loading" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
    {{ errorMessage }}
  </div>

  <!-- Lista de direcciones -->
  <div *ngIf="!loading && !errorMessage" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
    <div *ngFor="let direccion of direcciones" class="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition">
      <!-- Tipo de referencia -->
      <div class="flex justify-between items-start mb-3">
        <span 
          [class]="'px-3 py-1 rounded-full text-sm font-semibold ' + 
                   (direccion.tipoReferencia === 'CASA' ? 'bg-blue-100 text-blue-800' : 
                    direccion.tipoReferencia === 'TRABAJO' ? 'bg-green-100 text-green-800' : 
                    'bg-gray-100 text-gray-800')">
          {{ direccion.tipoReferencia }}
        </span>
      </div>

      <!-- Información de la dirección -->
      <div class="space-y-2 mb-4">
        <p class="text-gray-800 font-semibold">{{ direccion.calle }}</p>
        <p class="text-gray-600 text-sm">{{ direccion.barrio }}, {{ direccion.ciudad }}</p>
        <p *ngIf="direccion.referencia" class="text-gray-500 text-sm italic">{{ direccion.referencia }}</p>
      </div>

      <!-- Acciones -->
      <div class="flex space-x-2">
        <button 
          (click)="editarDireccion(direccion.id!)"
          class="flex-1 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded transition">
          Editar
        </button>
        <button 
          (click)="eliminarDireccion(direccion.id!)"
          class="flex-1 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded transition">
          Eliminar
        </button>
      </div>
    </div>
  </div>

  <!-- Mensaje cuando no hay direcciones -->
  <div *ngIf="!loading && !errorMessage && direcciones.length === 0" class="text-center py-12">
    <p class="text-gray-600 text-lg mb-4">No tienes direcciones guardadas</p>
    <button 
      (click)="agregarDireccion()"
      class="bg-teal-600 hover:bg-teal-700 text-white px-6 py-3 rounded-lg transition">
      Agregar tu primera dirección
    </button>
  </div>
</div>
