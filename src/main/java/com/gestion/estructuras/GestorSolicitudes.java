package com.gestion.estructuras;

import com.gestion.model.EstadoSolicitud;
import com.gestion.model.Solicitud;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GestorSolicitudes {
    private final ArrayList<Solicitud> solicitudes = new ArrayList<>();
    private final Queue<Solicitud> colaPendientes = new LinkedList<>();
    private final Stack<Solicitud> historial = new Stack<>();

    public void agregarSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
            colaPendientes.offer(solicitud);
        }
    }

    public List<Solicitud> obtenerTodas() {
        return new ArrayList<>(solicitudes);
    }

    public Solicitud verSiguiente() {
        return colaPendientes.peek();
    }

    public Solicitud procesarSiguiente() {
        Solicitud solicitud = colaPendientes.poll();
        if (solicitud == null) {
            return null;
        }
        solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitud.setEstado(EstadoSolicitud.ATENDIDA);
        historial.push(solicitud);
        return solicitud;
    }

    public List<Solicitud> procesarTodas() {
        List<Solicitud> procesadas = new ArrayList<>();
        while (!colaPendientes.isEmpty()) {
            procesadas.add(procesarSiguiente());
        }
        return procesadas;
    }

    public List<Solicitud> obtenerCola() {
        return new ArrayList<>(colaPendientes);
    }

    public int tamanoCola() {
        return colaPendientes.size();
    }

    public Solicitud ultimaProcesada() {
        return historial.isEmpty() ? null : historial.peek();
    }

    public List<Solicitud> obtenerHistorial() {
        List<Solicitud> resultado = new ArrayList<>();
        for (int i = historial.size() - 1; i >= 0; i--) {
            resultado.add(historial.get(i));
        }
        return resultado;
    }

    public Solicitud busquedaLineal(Long id) {
        for (Solicitud s : solicitudes) {
            if (s.getId() != null && s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public Solicitud busquedaBinaria(Long id) {
        List<Solicitud> ordenadas = ordenarPorIdInsercion();
        int inicio = 0;
        int fin = ordenadas.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            long idMedio = ordenadas.get(medio).getId();
            if (idMedio == id) {
                return ordenadas.get(medio);
            } else if (idMedio < id) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return null;
    }

    public List<Solicitud> ordenarPorPrioridadBurbuja() {
        List<Solicitud> lista = new ArrayList<>(solicitudes);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                Solicitud a = lista.get(j);
                Solicitud b = lista.get(j + 1);
                boolean debeIntercambiar = a.getPrioridad() < b.getPrioridad()
                        || (a.getPrioridad().equals(b.getPrioridad()) && a.getId() > b.getId());
                if (debeIntercambiar) {
                    lista.set(j, b);
                    lista.set(j + 1, a);
                    huboCambio = true;
                }
            }
            if (!huboCambio) {
                break;
            }
        }
        return lista;
    }

    public List<Solicitud> ordenarPorIdInsercion() {
        List<Solicitud> lista = new ArrayList<>(solicitudes);
        for (int i = 1; i < lista.size(); i++) {
            Solicitud actual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).getId() > actual.getId()) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
        return lista;
    }

    public void limpiar() {
        solicitudes.clear();
        colaPendientes.clear();
        historial.clear();
    }
}
