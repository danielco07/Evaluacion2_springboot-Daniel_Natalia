package com.gestion.service;

import com.gestion.dto.SolicitudRequest;
import com.gestion.estructuras.GestorSolicitudes;
import com.gestion.exception.RecursoNoEncontradoException;
import com.gestion.model.Cliente;
import com.gestion.model.Solicitud;
import com.gestion.model.Usuario;
import com.gestion.repository.SolicitudRepository;
import com.gestion.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudes gestor;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            UsuarioRepository usuarioRepository,
                            GestorSolicitudes gestor) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestor = gestor;
    }

    @Transactional
    public Solicitud crear(SolicitudRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + request.getClienteId()));

        if (!(usuario instanceof Cliente cliente)) {
            throw new IllegalArgumentException("Solo los clientes pueden crear solicitudes");
        }

        Solicitud solicitud = new Solicitud(request.getDescripcion().trim(), request.getPrioridad(), cliente);
        Solicitud guardada = solicitudRepository.save(solicitud);

        cliente.setPuntosFidelidad(cliente.getPuntosFidelidad() + 10);
        usuarioRepository.save(cliente);

        gestor.agregarSolicitud(guardada);
        return guardada;
    }

    public List<Solicitud> listar() {
        return solicitudRepository.findAll();
    }

    public Page<Solicitud> listarPaginado(Pageable pageable) {
        return solicitudRepository.findAll(pageable);
    }

    public Solicitud buscarPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud con id " + id));
    }

    @Transactional
    public Solicitud procesarSiguiente() {
        Solicitud procesada = gestor.procesarSiguiente();
        if (procesada == null) {
            throw new IllegalArgumentException("No hay solicitudes pendientes en la cola");
        }
        return solicitudRepository.save(procesada);
    }

    @Transactional
    public List<Solicitud> procesarTodas() {
        List<Solicitud> procesadas = gestor.procesarTodas();
        return solicitudRepository.saveAll(procesadas);
    }

    public List<Solicitud> verCola() {
        return gestor.obtenerCola();
    }

    public List<Solicitud> verHistorial() {
        return gestor.obtenerHistorial();
    }

    public List<Solicitud> ordenadasPorPrioridad() {
        return gestor.ordenarPorPrioridadBurbuja();
    }

    public Solicitud buscarEnGestor(Long id, String metodo) {
        Solicitud encontrada;
        if ("lineal".equalsIgnoreCase(metodo)) {
            encontrada = gestor.busquedaLineal(id);
        } else {
            encontrada = gestor.busquedaBinaria(id);
        }
        if (encontrada == null) {
            throw new RecursoNoEncontradoException("No existe la solicitud con id " + id + " en el gestor");
        }
        return encontrada;
    }
}
