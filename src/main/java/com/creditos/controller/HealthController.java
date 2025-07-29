package com.creditos.controller;

import com.creditos.dto.HealthResponseDTO;
import com.creditos.exception.CreditoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para health check da aplicação
 * Separado seguindo o princípio Single Responsibility
 * Atualizado com tratamento de erros padronizado
 *
 * @author Ednilton Curt Rauh
 * @version 1.1.0
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private static final String SERVICE_NAME = "API de Consulta de Créditos ISSQN";
    private static final String VERSION = "1.1.0";

    /**
     * Endpoint para verificar se a API está funcionando
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Health check da API",
            description = "Verifica se a API está funcionando corretamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "API funcionando corretamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno - API com problemas",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<HealthResponseDTO> healthCheck() {
        logger.debug("Health check solicitado");

        try {
            // Verificações básicas de saúde da aplicação
            verificarStatusBasico();

            HealthResponseDTO health = HealthResponseDTO.createHealthyResponse(SERVICE_NAME, VERSION);

            logger.debug("Health check concluído com sucesso");
            return ResponseEntity.ok(health);

        } catch (Exception ex) {
            logger.error("Falha no health check básico: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação de saúde da API: " + ex.getMessage());
        }
    }

    /**
     * Endpoint detalhado de health check
     */
    @GetMapping(value = "/detailed", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Health check detalhado",
            description = "Informações detalhadas sobre o status da API com verificações mais profundas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações detalhadas da API - sistema saudável",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno - problemas detectados no sistema",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<HealthResponseDTO> detailedHealthCheck() {
        logger.debug("Health check detalhado solicitado");

        try {
            // Verificações básicas
            verificarStatusBasico();

            // Verificações mais profundas
            verificarRecursosDetalhados();

            HealthResponseDTO health = HealthResponseDTO.createHealthyResponse(SERVICE_NAME, VERSION);

            logger.info("Health check detalhado concluído com sucesso");
            return ResponseEntity.ok(health);

        } catch (Exception ex) {
            logger.error("Falha no health check detalhado: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação detalhada de saúde: " + ex.getMessage());
        }
    }

    /**
     * Verificações básicas de status da aplicação
     */
    private void verificarStatusBasico() {
        try {
            // Verificação de memória disponível
            Runtime runtime = Runtime.getRuntime();
            long memoriaTotal = runtime.totalMemory();
            long memoriaLivre = runtime.freeMemory();
            long memoriaUsada = memoriaTotal - memoriaLivre;

            // Se estiver usando mais de 90% da memória, algo pode estar errado
            double percentualUso = (double) memoriaUsada / memoriaTotal * 100;
            if (percentualUso > 90) {
                logger.warn("HEALTH | Alto uso de memória detectado: {:.2f}%", percentualUso);
                throw new RuntimeException("Alto uso de memória: " + String.format("%.2f%%", percentualUso));
            }

            // Verificação básica de thread
            int threadsAtivas = Thread.activeCount();
            if (threadsAtivas > 100) { // Limite arbitrário para exemplo
                logger.warn("HEALTH | Alto número de threads ativas: {}", threadsAtivas);
                throw new RuntimeException("Alto número de threads ativas: " + threadsAtivas);
            }

            logger.debug("HEALTH | Verificações básicas OK - Memória: {:.2f}%, Threads: {}",
                    percentualUso, threadsAtivas);

        } catch (Exception ex) {
            logger.error("HEALTH | Falha nas verificações básicas: {}", ex.getMessage());
            throw new RuntimeException("Verificações básicas falharam: " + ex.getMessage(), ex);
        }
    }

    /**
     * Verificações mais detalhadas de recursos
     */
    private void verificarRecursosDetalhados() {
        try {
            // Verificação de espaço em disco (exemplo)
            verificarEspacoDisco();

            // Aqui poderiam ser adicionadas outras verificações como:
            // - Conectividade com banco de dados
            // - Serviços externos
            // - Filas de mensagem
            // - Cache
            // etc.

            logger.debug("HEALTH | Verificações detalhadas concluídas com sucesso");

        } catch (Exception ex) {
            logger.error("HEALTH | Falha nas verificações detalhadas: {}", ex.getMessage());
            throw new RuntimeException("Verificações detalhadas falharam: " + ex.getMessage(), ex);
        }
    }

    /**
     * Verifica espaço disponível em disco
     */
    private void verificarEspacoDisco() {
        try {
            java.io.File root = new java.io.File("/");
            long espacoTotal = root.getTotalSpace();
            long espacoLivre = root.getFreeSpace();

            if (espacoTotal > 0) {
                double percentualLivre = (double) espacoLivre / espacoTotal * 100;

                // Se menos de 10% de espaço livre, pode ser um problema
                if (percentualLivre < 10) {
                    logger.warn("HEALTH | Pouco espaço em disco: {:.2f}% livre", percentualLivre);
                    throw new RuntimeException("Pouco espaço em disco: " + String.format("%.2f%% livre", percentualLivre));
                }

                logger.debug("HEALTH | Espaço em disco OK: {:.2f}% livre", percentualLivre);
            }

        } catch (SecurityException ex) {
            // Em alguns ambientes, pode não ter permissão para verificar disco
            logger.debug("HEALTH | Não foi possível verificar espaço em disco (sem permissão)");
        } catch (Exception ex) {
            logger.warn("HEALTH | Erro na verificação de disco: {}", ex.getMessage());
            throw new RuntimeException("Falha na verificação de espaço em disco: " + ex.getMessage(), ex);
        }
    }
}