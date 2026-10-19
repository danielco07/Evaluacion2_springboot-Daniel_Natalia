package com.gestion.model;

public class Empleado extends Usuario {

    private String cargo;
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