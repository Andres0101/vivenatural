package com.example.andres_bonilla.ensayo.activity.classes;

/**
 * Created by ANDRES_BONILLA on 6/04/16.
 */
public class MarketProduct {

    private String nombre;
    //private int image;
    private int precio;

    public MarketProduct() {}

    public MarketProduct(String nombre, int precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }
}
