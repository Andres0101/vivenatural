package com.example.andres_bonilla.ensayo.activity.classes;

public class Reserve {

    private String producto;
    private String reservadoPor;
    private String reservadoA;
    private String imagenProducto;
    private double cantidadReservada;
    private int precio;
    private long fechaReserva;

    public Reserve() {}

    public Reserve(String producto, String reservadoPor, String reservadoA, String imagenProducto, double cantidadReservada, int precio, long fechaReserva) {
        this.producto = producto;
        this.reservadoPor = reservadoPor;
        this.reservadoA = reservadoA;
        this.imagenProducto = imagenProducto;
        this.cantidadReservada = cantidadReservada;
        this.precio = precio;
        this.fechaReserva = fechaReserva;
    }

    public String getProducto() {
        return producto;
    }

    public String getReservadoPor() {
        return reservadoPor;
    }

    public String getReservadoA() {
        return reservadoA;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public double getCantidadReservada() {
        return cantidadReservada;
    }

    public int getPrecio() {
        return precio;
    }

    public long getFechaReserva() {
        return fechaReserva;
    }
}
