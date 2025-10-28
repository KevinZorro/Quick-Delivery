package com.ufps.Quick_Delivery.model;

public enum Categoria {
    COMIDA_RAPIDA(1, "Comida Rápida"),
    ITALIANA(2, "Italiana"),
    MEXICANA(3, "Mexicana"),
    CHINA(4, "China"),
    VEGETARIANA(5, "Vegetariana"),
    POSTRES(6, "Postres"),
    BEBIDAS(7, "Bebidas"),
    MARISCOS(8, "Mariscos"),
    PARRILLA(9, "Parrilla"),
    OTROS(10, "Otros");

    private final int id;
    private final String nombre;

    Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
