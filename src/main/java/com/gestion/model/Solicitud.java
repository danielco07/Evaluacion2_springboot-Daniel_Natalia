package com.gestion.model;

import java.time.LocalDateTime;

/**
 * Solicitud de atención que crea un cliente
 * y que es gestionada por los empleados.
 */
public class Solicitud {

    private Long id;
    private String descripcion;
    // 1 = baja, 2 = media, 3 = alta
    private Integer prioridad = 1;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private LocalDateTime fechaCreacion;

    public Solicitud() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Solicitud(String descripcion, Integer prioridad) {
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Solicitud#" + id + " [" + estado + ", prioridad " + prioridad + "] " + descripcion;
    }
}
