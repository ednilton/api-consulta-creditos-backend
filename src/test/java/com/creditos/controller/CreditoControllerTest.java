package com.creditos.controller;

import com.creditos.dto.CreditoResponseDTO;
import com.creditos.controller.CreditoController.ExistenceResponse;
import com.creditos.exception.CreditoException;
import com.creditos.service.CreditoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditoController.class)
public class CreditoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoService creditoService;

    @Test
    @DisplayName("GET /api/creditos/123456 - Deve retornar 200 OK com lista de créditos")
    void consultarCreditosPorNfse_deveRetornarLista() throws Exception {
        Mockito.when(creditoService.consultarCreditosPorNfse("123456"))
                .thenReturn(Collections.singletonList(new CreditoResponseDTO()));

        mockMvc.perform(get("/api/creditos/123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/creditos/credito/789 - Deve retornar 200 OK com crédito")
    void consultarCreditoPorNumero_deveRetornarCredito() throws Exception {
        Mockito.when(creditoService.consultarCreditoPorNumero("789"))
                .thenReturn(new CreditoResponseDTO());

        mockMvc.perform(get("/api/creditos/credito/789")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/creditos/exists/credito/101 - Deve retornar true")
    void verificarExistenciaCredito_deveRetornarTrue() throws Exception {
        Mockito.when(creditoService.existeCreditoPorNumero("101")).thenReturn(true);

        mockMvc.perform(get("/api/creditos/exists/credito/101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/creditos/exists/nfse/202 - Deve retornar true")
    void verificarExistenciaCreditoPorNfse_deveRetornarTrue() throws Exception {
        Mockito.when(creditoService.existeCreditoPorNfse("202")).thenReturn(true);

        mockMvc.perform(get("/api/creditos/exists/nfse/202")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
