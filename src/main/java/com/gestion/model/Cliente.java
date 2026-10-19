package com.gestion.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {

    private Integer puntosFidelidad = 0;

    // Un cliente puede tener múltiples solicitudes
    private List<Solicitud> solicitudes = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String nombre, String correo, Integer puntosFidelidad) {
        super(nombre, correo);
        this.puntosFidelidad = puntosFidelidad;
    }

    @Override
    public String obtenerDescripcion() {
        return "Cliente " + getNombre() + " con " + puntosFidelidad + " puntos de fidelidad";
    }

    public Integer getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(Integer puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }
}