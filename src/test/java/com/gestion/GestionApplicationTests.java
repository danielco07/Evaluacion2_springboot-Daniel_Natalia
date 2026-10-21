package com.gestion;

import com.gestion.estructuras.GestorSolicitudes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GestionApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private GestorSolicitudes gestor;

    private long crearCliente(String correo) throws Exception {
        String json = "{\"tipo\":\"CLIENTE\",\"nombre\":\"Cliente Prueba\",\"correo\":\"" + correo + "\",\"puntosFidelidad\":0}";
        String respuesta = mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode nodo = mapper.readTree(respuesta);
        return nodo.get("id").asLong();
    }

    private void crearSolicitud(long clienteId, String descripcion, int prioridad) throws Exception {
        String json = "{\"descripcion\":\"" + descripcion + "\",\"prioridad\":" + prioridad + ",\"clienteId\":" + clienteId + "}";
        mvc.perform(post("/solicitudes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void creaLosTresTiposDeUsuarioConPolimorfismo() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"ADMINISTRADOR\",\"nombre\":\"Admin\",\"correo\":\"admin@x.com\",\"nivelAcceso\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.descripcion").value(containsString("nivel de acceso 3")));

        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"EMPLEADO\",\"nombre\":\"Emp\",\"correo\":\"emp@x.com\",\"cargo\":\"Soporte\",\"salario\":1500000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value(startsWith("Empleado")));

        crearCliente("cli@x.com");

        mvc.perform(get("/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/usuarios/1")).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Admin"));
        mvc.perform(get("/usuarios/99")).andExpect(status().isNotFound());
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"CLIENTE\",\"nombre\":\"\",\"correo\":\"no-es-correo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.correo").exists());
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"EMPLEADO\",\"nombre\":\"E\",\"correo\":\"e@x.com\",\"cargo\":\"C\",\"salario\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.salario").exists());
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"ADMINISTRADOR\",\"nombre\":\"A\",\"correo\":\"a@x.com\",\"nivelAcceso\":7}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nivelAcceso").exists());
    }

    @Test
    void solicitudesColaPilaBusquedaYOrdenamiento() throws Exception {
        gestor.limpiar();
        long clienteId = crearCliente("pedro@x.com");
        crearSolicitud(clienteId, "Primera", 1);
        crearSolicitud(clienteId, "Segunda", 3);
        crearSolicitud(clienteId, "Tercera", 2);

        mvc.perform(get("/solicitudes")).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/solicitudes/2")).andExpect(jsonPath("$.descripcion").value("Segunda"))
                .andExpect(jsonPath("$.clienteId").value((int) clienteId));
        mvc.perform(get("/usuarios/" + clienteId + "/solicitudes")).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/usuarios/" + clienteId)).andExpect(jsonPath("$.puntosFidelidad").value(30));

        mvc.perform(get("/solicitudes/ordenadas"))
                .andExpect(jsonPath("$[0].descripcion").value("Segunda"))
                .andExpect(jsonPath("$[2].descripcion").value("Primera"));

        mvc.perform(get("/solicitudes/buscar/3")).andExpect(jsonPath("$.descripcion").value("Tercera"));
        mvc.perform(get("/solicitudes/buscar/1?metodo=lineal")).andExpect(jsonPath("$.descripcion").value("Primera"));

        mvc.perform(post("/solicitudes/procesar"))
                .andExpect(jsonPath("$.descripcion").value("Primera"))
                .andExpect(jsonPath("$.estado").value("ATENDIDA"));
        mvc.perform(get("/solicitudes/1")).andExpect(jsonPath("$.estado").value("ATENDIDA"));
        mvc.perform(get("/solicitudes/cola")).andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(get("/solicitudes/historial")).andExpect(jsonPath("$[0].descripcion").value("Primera"));

        mvc.perform(post("/solicitudes/procesar-todas")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(post("/solicitudes/procesar")).andExpect(status().isBadRequest());

        mvc.perform(get("/solicitudes/paginado?pagina=0&tamano=2&ordenarPor=prioridad&direccion=desc"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].prioridad").value(3));
    }

    @Test
    void soloClientesPuedenCrearSolicitudes() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"ADMINISTRADOR\",\"nombre\":\"Admin\",\"correo\":\"ad@x.com\",\"nivelAcceso\":1}"));
        mvc.perform(post("/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"X\",\"prioridad\":1,\"clienteId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Solo los clientes pueden crear solicitudes"));
    }
}
