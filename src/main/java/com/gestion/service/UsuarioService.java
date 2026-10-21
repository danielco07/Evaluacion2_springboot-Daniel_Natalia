package com.gestion.service;

import com.gestion.exception.RecursoNoEncontradoException;
import com.gestion.model.*;
import com.gestion.repository.SolicitudRepository;
import com.gestion.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, SolicitudRepository solicitudRepository) {
        this.usuarioRepository = usuarioRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public Usuario crear(Usuario usuario) {
        // if / else: validaciones de negocio
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo " + usuario.getCorreo());
        } else {
            usuario.setNombre(usuario.getNombre().trim());
            usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
        }

        // switch: reglas específicas según el tipo de usuario
        switch (tipoDe(usuario)) {
            case "ADMINISTRADOR":
                // nada adicional: @Min/@Max ya validan el nivel 1-5
                break;
            case "EMPLEADO":
                Empleado empleado = (Empleado) usuario;
                empleado.setCargo(empleado.getCargo().trim());
                break;
            case "CLIENTE":
                Cliente cliente = (Cliente) usuario;
                if (cliente.getPuntosFidelidad() == null) {
                    cliente.setPuntosFidelidad(0);
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no soportado");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    public List<Solicitud> solicitudesDeCliente(Long id) {
        Usuario usuario = buscarPorId(id);
        if (!(usuario instanceof Cliente)) {
            throw new IllegalArgumentException("El usuario " + id + " no es un cliente");
        }
        return solicitudRepository.findByCliente_Id(id);
    }

    public static String tipoDe(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return "ADMINISTRADOR";
        } else if (usuario instanceof Empleado) {
            return "EMPLEADO";
        } else if (usuario instanceof Cliente) {
            return "CLIENTE";
        }
        return "DESCONOCIDO";
    }
}
