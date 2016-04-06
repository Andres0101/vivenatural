package com.example.andres_bonilla.ensayo.activity.classes;

/**
 * Created by estudiante28 on 31/03/16.
 */
public class Product {

    private String productor;
    private int image;
    private String nombreProducto;
    private double cantidad;
    private int precio;
    private String descripcionProducto;

    public Product() {}

    public Product(String productor, int image, String nombreProducto, double cantidad, int precio, String descripcionProducto) {
        this.productor = productor;
        this.image = image;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcionProducto = descripcionProducto;
    }

    public String getProductor() {
        return productor;
    }

    public int getImage() {
        return image;
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
