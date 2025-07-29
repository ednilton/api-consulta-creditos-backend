package com.creditos.controller;

import com.creditos.dto.CreditoResponseDTO;
import com.creditos.exception.CreditoException;
import com.creditos.service.CreditoService;
import com.creditos.util.LoggingUtils;
import com.creditos.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Controller REST refatorado para consulta de créditos constituídos
 * Implementa EXATAMENTE os endpoints especificados no PDF do desafio técnico
 * Refatorado seguindo princípios SOLID, DRY e KISS
 * Atualizado com tratamento de erros padronizado e ValidationUtils
 *
 * Endpoints principais:
 * - GET /api/creditos/{numeroNfse}
 * - GET /api/creditos/credito/{numeroCredito}
 *
 * @author Ednilton Curt Rauh
 * @version 2.2.0
 */
@RestController
@RequestMapping("/api/creditos")
@Validated
@Tag(name = "Créditos ISSQN", description = "API para consulta de créditos constituídos de ISSQN")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreditoController {

    private static final Logger logger = LoggerFactory.getLogger(CreditoController.class);

    private final CreditoService creditoService;

    @Autowired
    public CreditoController(CreditoService creditoService) {
        this.creditoService = creditoService;
    }

    /**
     * Endpoint: GET /api/creditos/{numeroNfse}
     * Descrição: Retorna uma lista de créditos constituídos com base no número da NFS-e
     *
     * @param numeroNfse Número identificador da NFS-e
     * @return Lista de créditos encontrados
     */
    @GetMapping(value = "/{numeroNfse}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Consultar créditos por número da NFS-e",
            description = "Retorna uma lista de créditos constituídos com base no número da NFS-e fornecido"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Créditos encontrados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro inválido - Número da NFS-e é obrigatório",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nenhum crédito encontrado para a NFS-e informada",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<CreditoResponseDTO>> consultarCreditosPorNfse(
            @Parameter(
                    description = "Número identificador da NFS-e",
                    required = true,
                    example = "7891011"
            )
            @PathVariable
            @NotBlank(message = "Número da NFS-e é obrigatório")
            @Size(min = 1, max = 50, message = "Número da NFS-e deve ter entre 1 e 50 caracteres")
            String numeroNfse) {

        LoggingUtils.logSolicitacaoRecebida(logger, "consulta por NFS-e", numeroNfse);

        try {
            // Validação usando ValidationUtils
            ValidationUtils.validateNumeroNfse(numeroNfse);

            List<CreditoResponseDTO> creditos = creditoService.consultarCreditosPorNfse(numeroNfse);

            LoggingUtils.logOperacaoFinalizada(logger, "Consulta por NFS-e " + numeroNfse, creditos.size());

            return ResponseEntity.ok(creditos);

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro na consulta por NFS-e {}: {}", numeroNfse, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta de créditos por NFS-e: " + ex.getMessage());
        }
    }

    /**
     * Endpoint: GET /api/creditos/credito/{numeroCredito}
     * Descrição: Retorna os detalhes de um crédito constituído específico com base no número do crédito
     *
     * @param numeroCredito Número identificador do crédito constituído
     * @return Dados do crédito encontrado
     */
    @GetMapping(value = "/credito/{numeroCredito}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Consultar crédito por número do crédito",
            description = "Retorna os detalhes de um crédito constituído específico com base no número do crédito fornecido"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Crédito encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro inválido - Número do crédito é obrigatório",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Crédito não encontrado com o número informado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<CreditoResponseDTO> consultarCreditoPorNumero(
            @Parameter(
                    description = "Número identificador do crédito constituído",
                    required = true,
                    example = "123456"
            )
            @PathVariable
            @NotBlank(message = "Número do crédito é obrigatório")
            @Size(min = 1, max = 50, message = "Número do crédito deve ter entre 1 e 50 caracteres")
            String numeroCredito) {

        LoggingUtils.logSolicitacaoRecebida(logger, "consulta por número do crédito", numeroCredito);

        try {
            // Validação usando ValidationUtils
            ValidationUtils.validateNumeroCredito(numeroCredito);

            CreditoResponseDTO credito = creditoService.consultarCreditoPorNumero(numeroCredito);

            LoggingUtils.logOperacaoFinalizada(logger, "Consulta por número do crédito " + numeroCredito, 1);

            return ResponseEntity.ok(credito);

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro na consulta por número do crédito {}: {}", numeroCredito, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta do crédito: " + ex.getMessage());
        }
    }

    /**
     * Endpoint para verificar se um crédito existe
     * GET /api/creditos/exists/credito/{numeroCredito}
     */
    @GetMapping(value = "/exists/credito/{numeroCredito}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Verificar se crédito existe",
            description = "Verifica se existe um crédito com o número informado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificação realizada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro inválido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ExistenceResponse> verificarExistenciaCredito(
            @Parameter(
                    description = "Número do crédito a ser verificado",
                    required = true,
                    example = "123456"
            )
            @PathVariable
            @NotBlank(message = "Número do crédito é obrigatório")
            String numeroCredito) {

        LoggingUtils.logSolicitacaoRecebida(logger, "verificação de existência", numeroCredito);

        try {
            // Validação usando ValidationUtils
            ValidationUtils.validateNumeroCredito(numeroCredito);

            boolean existe = creditoService.existeCreditoPorNumero(numeroCredito);

            ExistenceResponse response = new ExistenceResponse(existe, numeroCredito, "credito");

            LoggingUtils.logOperacaoFinalizada(logger, "Verificação de existência", null);

            return ResponseEntity.ok(response);

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro na verificação de existência do crédito {}: {}", numeroCredito, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação de existência: " + ex.getMessage());
        }
    }

    /**
     * Endpoint para verificar se existem créditos para uma NFS-e
     * GET /api/creditos/exists/nfse/{numeroNfse}
     */
    @GetMapping(value = "/exists/nfse/{numeroNfse}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Verificar se existem créditos para NFS-e",
            description = "Verifica se existem créditos para o número da NFS-e informado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificação realizada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro inválido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ExistenceResponse> verificarExistenciaCreditosPorNfse(
            @Parameter(
                    description = "Número da NFS-e a ser verificado",
                    required = true,
                    example = "7891011"
            )
            @PathVariable
            @NotBlank(message = "Número da NFS-e é obrigatório")
            String numeroNfse) {

        LoggingUtils.logSolicitacaoRecebida(logger, "verificação de existência por NFS-e", numeroNfse);

        try {
            // Validação usando ValidationUtils
            ValidationUtils.validateNumeroNfse(numeroNfse);

            boolean existe = creditoService.existeCreditoPorNfse(numeroNfse);

            ExistenceResponse response = new ExistenceResponse(existe, numeroNfse, "nfse");

            LoggingUtils.logOperacaoFinalizada(logger, "Verificação de existência por NFS-e", null);

            return ResponseEntity.ok(response);

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro na verificação de existência por NFS-e {}: {}", numeroNfse, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação de existência por NFS-e: " + ex.getMessage());
        }
    }

    /**
     * Classe interna para resposta de verificação de existência
     */
    public static class ExistenceResponse {
        private boolean exists;
        private String value;
        private String type;
        private long timestamp;

        public ExistenceResponse(boolean exists, String value, String type) {
            this.exists = exists;
            this.value = value;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters e Setters
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}