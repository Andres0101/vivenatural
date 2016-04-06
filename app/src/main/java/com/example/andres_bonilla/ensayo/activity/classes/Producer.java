package com.example.andres_bonilla.ensayo.activity.classes;

/**
 * Created by ANDRES_BONILLA on 4/04/16.
 */
public class Producer {
    private String nombre;
    private String correo;
    private String rol;
    private String descripcion;

    public Producer() {}

    public Producer(String nombre, String correo, String rol, String descripcion) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.descripcion = descripcion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
