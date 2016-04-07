package com.example.andres_bonilla.ensayo.activity.classes;

import android.graphics.Bitmap;

/**
 * Created by ANDRES_BONILLA on 31/03/16.
 */
public class Product {

    private String productor;
    private Bitmap imagen;
    private String nombreProducto;
    private double cantidad;
    private int precio;
    private String descripcionProducto;

    public Product() {}

    public Product(String productor, Bitmap imagen, String nombreProducto, double cantidad, int precio, String descripcionProducto) {
        this.productor = productor;
        this.imagen = imagen;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcionProducto = descripcionProducto;
    }

    public String getProductor() {
        return productor;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }
}
