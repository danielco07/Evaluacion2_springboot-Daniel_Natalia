package com.gestion.repository;

import com.gestion.model.EstadoSolicitud;
import com.gestion.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByClienteId(Long clienteId);

    List<Solicitud> findByEstadoOrderByIdAsc(EstadoSolicitud estado);
}