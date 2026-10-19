package com.gestion.model;

public class Administrador extends Usuario {

    private Integer nivelAcceso;

    public Administrador() {
    }

    public Administrador(String nombre, String correo, Integer nivelAcceso) {
        super(nombre, correo);
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String obtenerDescripcion() {
        return "Administrador " + getNombre() + " con nivel de acceso " + nivelAcceso;
    }

    public Integer getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(Integer nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }
}