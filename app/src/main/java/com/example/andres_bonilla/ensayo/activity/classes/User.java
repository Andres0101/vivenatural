package com.example.andres_bonilla.ensayo.activity.classes;

/**
 * Created by ANDRES_BONILLA on 25/02/2016.
 */
public class User {
    private String nombre;
    private String imagen;
    private String correo;
    private String rol;
    private String descripcion;

    public User() {}

    public User(String nombre, String imagen, String correo, String rol, String descripcion) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.correo = correo;
        this.rol = rol;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
