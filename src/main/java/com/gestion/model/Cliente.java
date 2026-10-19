package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    private Integer puntosFidelidad = 0;

    /**
     * Relación Cliente → Solicitudes (uno a muchos).
     * Un cliente puede tener múltiples solicitudes.
     * @JsonIgnore evita ciclos infinitos al convertir a JSON.
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
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
