package com.gestion.controller;

import com.gestion.model.Solicitud;
import com.gestion.model.Usuario;
import com.gestion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuario));
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @GetMapping("/paginado")
    public Page<Usuario> listarPaginado(@RequestParam(defaultValue = "0") int pagina,
                                        @RequestParam(defaultValue = "5") int tamano,
                                        @RequestParam(defaultValue = "id") String ordenarPor,
                                        @RequestParam(defaultValue = "asc") String direccion) {
        Sort orden = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();
        return usuarioService.listarPaginado(PageRequest.of(pagina, tamano, orden));
    }

    @GetMapping("/{id}/solicitudes")
    public List<Solicitud> solicitudesDeCliente(@PathVariable Long id) {
        return usuarioService.solicitudesDeCliente(id);
    }
}
