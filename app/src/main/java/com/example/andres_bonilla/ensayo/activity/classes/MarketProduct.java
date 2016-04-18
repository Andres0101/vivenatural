package com.example.andres_bonilla.ensayo.activity.classes;

public class MarketProduct {

    private String nombre;
    private String imagen;
    private int precio;

    public MarketProduct() {}

    public MarketProduct(String nombre, String imagen, int precio) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public int getPrecio() {
        return precio;
    }
}
