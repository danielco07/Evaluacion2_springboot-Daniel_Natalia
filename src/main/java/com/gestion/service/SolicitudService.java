package com.gestion.service;

import com.gestion.dto.SolicitudRequest;
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

    public SolicitudService(SolicitudRepository solicitudRepository,
                            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Solicitud crear(SolicitudRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + request.getClienteId()));

        // Solo los clientes pueden crear solicitudes
        if (!(usuario instanceof Cliente cliente)) {
            throw new IllegalArgumentException("Solo los clientes pueden crear solicitudes");
        }

        Solicitud solicitud = new Solicitud(request.getDescripcion().trim(), request.getPrioridad(), cliente);
        Solicitud guardada = solicitudRepository.save(solicitud);

        // Se suma 10 puntos de fidelidad por cada solicitud
        cliente.setPuntosFidelidad(cliente.getPuntosFidelidad() + 10);
        usuarioRepository.save(cliente);

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
}
