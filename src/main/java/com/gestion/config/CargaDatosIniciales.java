package com.gestion.config;

import com.gestion.estructuras.GestorSolicitudes;
import com.gestion.model.*;
import com.gestion.repository.SolicitudRepository;
import com.gestion.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class CargaDatosIniciales implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;
    private final GestorSolicitudes gestor;

    public CargaDatosIniciales(UsuarioRepository usuarioRepository,
                               SolicitudRepository solicitudRepository,
                               GestorSolicitudes gestor) {
        this.usuarioRepository = usuarioRepository;
        this.solicitudRepository = solicitudRepository;
        this.gestor = gestor;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            List<Solicitud> guardadas = solicitudRepository.findAll(Sort.by("id"));
            for (Solicitud s : guardadas) {
                gestor.agregarSolicitud(s);
            }
            System.out.println(">> Ya existen datos: " + guardadas.size() + " solicitudes cargadas al gestor, "
                    + gestor.tamanoCola() + " pendientes en la cola.");
            return;
        }

        String[][] datos = {
                {"ADMINISTRADOR", "Laura Gómez", "laura@empresa.com", "5"},
                {"EMPLEADO", "Carlos Ruiz", "carlos@empresa.com", "Soporte técnico"},
                {"EMPLEADO", "Ana Torres", "ana@empresa.com", "Atención al cliente"},
                {"CLIENTE", "Pedro Martínez", "pedro@correo.com", "0"},
                {"CLIENTE", "Sofía Ramírez", "sofia@correo.com", "20"}
        };

        for (String[] fila : datos) {
            Usuario usuario;
            switch (fila[0]) {
                case "ADMINISTRADOR":
                    usuario = new Administrador(fila[1], fila[2], Integer.parseInt(fila[3]));
                    break;
                case "EMPLEADO":
                    usuario = new Empleado(fila[1], fila[2], fila[3], 2500000.0);
                    break;
                case "CLIENTE":
                    usuario = new Cliente(fila[1], fila[2], Integer.parseInt(fila[3]));
                    break;
                default:
                    System.out.println(">> Tipo desconocido: " + fila[0]);
                    continue;
            }
            usuarioRepository.save(usuario);
            System.out.println(">> Cargado: " + usuario.obtenerDescripcion());
        }

        List<Usuario> usuarios = usuarioRepository.findAll();
        String[] descripciones = {"No puedo iniciar sesión", "Error en la factura", "Solicitud de devolución"};
        int prioridad = 1;
        for (Usuario u : usuarios) {
            if (u instanceof Cliente cliente) {
                for (String descripcion : descripciones) {
                    Solicitud s = solicitudRepository.save(new Solicitud(descripcion, prioridad, cliente));
                    gestor.agregarSolicitud(s);
                    prioridad = (prioridad % 3) + 1;
                }
            }
        }

        int contador = 0;
        List<Solicitud> cola = gestor.obtenerCola();
        while (contador < cola.size()) {
            System.out.println(">> En cola [" + (contador + 1) + "]: " + cola.get(contador));
            contador++;
        }
        System.out.println(">> Datos iniciales cargados: " + usuarios.size() + " usuarios, "
                + gestor.tamanoCola() + " solicitudes pendientes.");
    }
}
