package com.gestion.controller;

import com.gestion.dto.SolicitudRequest;
import com.gestion.model.Solicitud;
import com.gestion.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // POST /solicitudes
    @PostMapping
    public ResponseEntity<Solicitud> crear(@Valid @RequestBody SolicitudRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crear(request));
    }

    // GET /solicitudes
    @GetMapping
    public List<Solicitud> listar() {
        return solicitudService.listar();
    }

    // GET /solicitudes/{id}
    @GetMapping("/{id}")
    public Solicitud buscarPorId(@PathVariable Long id) {
        return solicitudService.buscarPorId(id);
    }

    // GET /solicitudes/paginado?pagina=0&tamano=5&ordenarPor=prioridad&direccion=desc
    @GetMapping("/paginado")
    public Page<Solicitud> listarPaginado(@RequestParam(defaultValue = "0") int pagina,
                                          @RequestParam(defaultValue = "5") int tamano,
                                          @RequestParam(defaultValue = "id") String ordenarPor,
                                          @RequestParam(defaultValue = "asc") String direccion) {
        Sort orden = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();
        return solicitudService.listarPaginado(PageRequest.of(pagina, tamano, orden));
    }
}
