package com.gestion.model;

/**
 * Clase base de todos los usuarios del sistema.
 * Encapsulación: los atributos son privados y se accede
 * a ellos solo mediante getters y setters.
 */
public class Usuario {

    private Long id;
    private String nombre;
    private String correo;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
