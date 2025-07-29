package com.creditos.controller;

import com.creditos.dto.CreditoResponseDTO;
import com.creditos.exception.CreditoException;
import com.creditos.service.CreditoService;
import com.creditos.util.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para endpoints administrativos
 * Separado seguindo o princípio Single Responsibility
 * Atualizado com tratamento de erros padronizado
 *
 * @author Ednilton Curt Rauh
 * @version 1.1.0
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administração", description = "Endpoints administrativos para gestão de créditos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final CreditoService creditoService;

    @Autowired
    public AdminController(CreditoService creditoService) {
        this.creditoService = creditoService;
    }

    /**
     * Endpoint para listar todos os créditos (útil para testes e administração)
     */
    @GetMapping(value = "/creditos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar todos os créditos",
            description = "Retorna todos os créditos cadastrados (endpoint auxiliar para testes e administração)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de créditos recuperada com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<CreditoResponseDTO>> listarTodosCreditos() {
        LoggingUtils.logSolicitacaoRecebida(logger, "listar todos os créditos", "N/A");

        try {
            List<CreditoResponseDTO> creditos = creditoService.listarTodosCreditos();

            // Verifica se a lista está vazia (caso específico administrativo)
            if (creditos.isEmpty()) {
                LoggingUtils.logOperacaoFinalizada(logger, "Listagem completa - nenhum crédito encontrado", 0);
                logger.warn("ADMIN | Nenhum crédito encontrado na base de dados");
            } else {
                LoggingUtils.logOperacaoFinalizada(logger, "Listagem completa", creditos.size());
            }

            return ResponseEntity.ok(creditos);

        } catch (Exception ex) {
            logger.error("Erro ao listar todos os créditos: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na listagem administrativa de créditos: " + ex.getMessage());
        }
    }

    /**
     * Endpoint para verificar estatísticas dos créditos
     */
    @GetMapping(value = "/estatisticas", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Estatísticas dos créditos",
            description = "Retorna estatísticas básicas dos créditos cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estatísticas recuperadas com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<EstatisticasDTO> obterEstatisticas() {
        LoggingUtils.logSolicitacaoRecebida(logger, "estatísticas", "N/A");

        try {
            List<CreditoResponseDTO> todosCreditos = creditoService.listarTodosCreditos();

            EstatisticasDTO estatisticas = new EstatisticasDTO(
                    todosCreditos.size(),
                    System.currentTimeMillis()
            );

            LoggingUtils.logOperacaoFinalizada(logger, "Consulta de estatísticas", null);

            return ResponseEntity.ok(estatisticas);

        } catch (Exception ex) {
            logger.error("Erro ao obter estatísticas: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na geração de estatísticas: " + ex.getMessage());
        }
    }

    /**
     * DTO interno para estatísticas
     */
    public static class EstatisticasDTO {
        private int totalCreditos;
        private long timestamp;

        public EstatisticasDTO(int totalCreditos, long timestamp) {
            this.totalCreditos = totalCreditos;
            this.timestamp = timestamp;
        }

        // Getters
        public int getTotalCreditos() { return totalCreditos; }
        public long getTimestamp() { return timestamp; }

        // Setters
        public void setTotalCreditos(int totalCreditos) { this.totalCreditos = totalCreditos; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}