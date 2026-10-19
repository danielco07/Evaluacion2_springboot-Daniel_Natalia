package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

/**
 * Clase base de todos los usuarios del sistema.
 * Es abstracta: no existe un "usuario" genérico, siempre es
 * Administrador, Empleado o Cliente.
 *
 * - Encapsulación: atributos privados con getters/setters.
 * - Herencia JPA: SINGLE_TABLE (una tabla "usuarios" con columna "tipo_usuario").
 * - JSON polimórfico: el campo "tipo" indica qué subclase crear en el POST.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Administrador.class, name = "ADMINISTRADOR"),
        @JsonSubTypes.Type(value = Empleado.class, name = "EMPLEADO"),
        @JsonSubTypes.Type(value = Cliente.class, name = "CLIENTE")
})
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String correo;

    // Constructor vacío (requerido por JPA y Jackson)
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    /**
     * Método polimórfico: cada subclase lo sobrescribe
     * con su propia descripción. Se incluye en el JSON como "descripcion".
     */
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
