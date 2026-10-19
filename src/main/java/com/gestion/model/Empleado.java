package com.gestion.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@DiscriminatorValue("EMPLEADO")
public class Empleado extends Usuario {

    @NotBlank(message = "El cargo no puede estar vacío")
    private String cargo;

    @NotNull(message = "El salario es obligatorio")
    @Positive(message = "El salario debe ser mayor que cero")
    private Double salario;

    public Empleado() {
    }

    public Empleado(String nombre, String correo, String cargo, Double salario) {
        super(nombre, correo);
        this.cargo = cargo;
        this.salario = salario;
    }

    @Override
    public String obtenerDescripcion() {
        return "Empleado " + getNombre() + " - cargo: " + cargo + ", salario: $" + salario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
