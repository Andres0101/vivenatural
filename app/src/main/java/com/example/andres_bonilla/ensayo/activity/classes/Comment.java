package com.example.andres_bonilla.ensayo.activity.classes;

/**
 * Created by ANDRES_BONILLA on 13/04/2016.
 */
public class Comment {
    private String dirigidoA;
    private String hechoPor;
    private String comentario;
    private String productoComentado;
    private String imagenConsumidor;

    public Comment() {}

    public Comment(String dirigidoA, String hechoPor, String comentario, String productoComentado, String imagenConsumidor) {
        this.dirigidoA = dirigidoA;
        this.hechoPor = hechoPor;
        this.comentario = comentario;
        this.productoComentado = productoComentado;
        this.imagenConsumidor = imagenConsumidor;
    }

    public String getDirigidoA() {
        return dirigidoA;
    }

    public String getHechoPor() {
        return hechoPor;
    }

    public String getComentario() {
        return comentario;
    }

    public String getProductoComentado() {
        return productoComentado;
    }

    public String getImagenConsumidor() {
        return imagenConsumidor;
    }
}
