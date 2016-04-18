package com.example.andres_bonilla.ensayo.activity.classes;

public class Product {

    private String productor;
    private String imagen;
    private String nombreProducto;
    private double cantidad;
    private int precio;
    private String descripcionProducto;

    public Product() {}

    public Product(String productor, String imagen, String nombreProducto, double cantidad, int precio, String descripcionProducto) {
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

    public String getImagen() {
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
