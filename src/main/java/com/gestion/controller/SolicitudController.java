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

    @PostMapping
    public ResponseEntity<Solicitud> crear(@Valid @RequestBody SolicitudRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crear(request));
    }

    @GetMapping
    public List<Solicitud> listar() {
        return solicitudService.listar();
    }

    @GetMapping("/{id}")
    public Solicitud buscarPorId(@PathVariable Long id) {
        return solicitudService.buscarPorId(id);
    }

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

    @GetMapping("/cola")
    public List<Solicitud> verCola() {
        return solicitudService.verCola();
    }

    @PostMapping("/procesar")
    public Solicitud procesarSiguiente() {
        return solicitudService.procesarSiguiente();
    }

    @PostMapping("/procesar-todas")
    public List<Solicitud> procesarTodas() {
        return solicitudService.procesarTodas();
    }

    @GetMapping("/historial")
    public List<Solicitud> verHistorial() {
        return solicitudService.verHistorial();
    }

    @GetMapping("/ordenadas")
    public List<Solicitud> ordenadasPorPrioridad() {
        return solicitudService.ordenadasPorPrioridad();
    }

    @GetMapping("/buscar/{id}")
    public Solicitud buscarEnGestor(@PathVariable Long id,
                                    @RequestParam(defaultValue = "binaria") String metodo) {
        return solicitudService.buscarEnGestor(id, metodo);
    }
}
