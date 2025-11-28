package com.ufps.Quick_Delivery.model;

public enum EstadoPromocion {
    ACTIVA,        // se puede usar (no expira y no superó usos)
    EXPIRADA,      // por fecha
    AGOTADA,       // llegó al límite de usos
    INACTIVA       // deshabilitada manualmente por el restaurante
}