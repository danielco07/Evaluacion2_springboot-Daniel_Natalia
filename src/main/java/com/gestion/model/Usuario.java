package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Administrador.class, name = "ADMINISTRADOR"),
        @JsonSubTypes.Type(value = Empleado.class, name = "EMPLEADO"),
        @JsonSubTypes.Type(value = Cliente.class, name = "CLIENTE")
})
public abstract class Usuario {

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

    @JsonProperty(value = "descripcion", access = JsonProperty.Access.READ_ONLY)
    public abstract String obtenerDescripcion();

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