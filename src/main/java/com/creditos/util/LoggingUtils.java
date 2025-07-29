package com.creditos.util;

import com.creditos.exception.CreditoExceptionBase;
import org.slf4j.Logger;

/**
 * Classe utilitária para padronizar logs
 * Implementa o princípio DRY evitando duplicação de código de logging
 * Atualizada para trabalhar com o novo sistema de exceções
 *
 * @author Ednilton Curt Rauh
 * @version 1.1.0
 */
public class LoggingUtils {

    private LoggingUtils() {
        // Construtor privado para classe utilitária
    }

    // ================================================
    // LOGS DE CONSULTA
    // ================================================

    /**
     * Loga o início de uma consulta
     *
     * @param logger Logger a ser usado
     * @param tipoConsulta Tipo da consulta (ex: "NFS-e", "número do crédito")
     * @param valor Valor sendo consultado
     */
    public static void logInicioConsulta(Logger logger, String tipoConsulta, String valor) {
        logger.info("CONSULTA | Iniciando consulta por {}: {}", tipoConsulta, valor);
    }

    /**
     * Loga o sucesso de uma consulta
     *
     * @param logger Logger a ser usado
     * @param tipoConsulta Tipo da consulta (ex: "NFS-e", "número do crédito")
     * @param valor Valor consultado
     * @param quantidade Quantidade de resultados encontrados
     */
    public static void logConsultaSucesso(Logger logger, String tipoConsulta, String valor, int quantidade) {
        logger.info("CONSULTA | Sucesso para {} {}: {} resultado(s) encontrado(s)",
                tipoConsulta, valor, quantidade);
    }

    /**
     * Loga quando nenhum resultado é encontrado
     *
     * @param logger Logger a ser usado
     * @param tipoConsulta Tipo da consulta
     * @param valor Valor consultado
     */
    public static void logNenhumResultado(Logger logger, String tipoConsulta, String valor) {
        logger.warn("CONSULTA | Nenhum resultado encontrado para {} {}", tipoConsulta, valor);
    }

    // ================================================
    // LOGS DE CONTROLLER
    // ================================================

    /**
     * Loga o recebimento de uma solicitação no controller
     *
     * @param logger Logger a ser usado
     * @param operacao Nome da operação/endpoint
     * @param parametro Parâmetro recebido
     */
    public static void logSolicitacaoRecebida(Logger logger, String operacao, String parametro) {
        logger.info("ENDPOINT | Solicitação recebida - Operação: {} | Parâmetro: {}", operacao, parametro);
    }

    /**
     * Loga a finalização de uma operação no controller
     *
     * @param logger Logger a ser usado
     * @param operacao Operação realizada
     * @param quantidade Quantidade de resultados (opcional)
     */
    public static void logOperacaoFinalizada(Logger logger, String operacao, Integer quantidade) {
        if (quantidade != null) {
            logger.info("ENDPOINT | Operação '{}' finalizada com {} resultado(s)", operacao, quantidade);
        } else {
            logger.info("ENDPOINT | Operação '{}' finalizada com sucesso", operacao);
        }
    }

    // ================================================
    // LOGS DE ERRO ESPECÍFICOS
    // ================================================

    /**
     * Loga erro específico com contexto
     *
     * @param logger Logger a ser usado
     * @param operacao Operação que gerou o erro
     * @param detalhes Detalhes do erro
     * @param parametro Parâmetro relacionado ao erro (opcional)
     */
    public static void logErro(Logger logger, String operacao, String detalhes, String parametro) {
        if (parametro != null) {
            logger.error("ERRO | Operação: {} | Parâmetro: {} | Detalhes: {}", operacao, parametro, detalhes);
        } else {
            logger.error("ERRO | Operação: {} | Detalhes: {}", operacao, detalhes);
        }
    }

    /**
     * Loga erro usando informações da CreditoExceptionBase
     *
     * @param logger Logger a ser usado
     * @param exception Exceção com informações estruturadas
     * @param contextoAdicional Contexto adicional (opcional)
     */
    public static void logErroException(Logger logger, CreditoExceptionBase exception, String contextoAdicional) {
        String contexto = contextoAdicional != null ? " | Contexto: " + contextoAdicional : "";

        logger.error("ERRO | Código: {} | Tipo: {} | Parâmetro: {} | Valor: {} | Mensagem: {}{}",
                exception.getCodigoErro(),
                exception.getTipoErro(),
                exception.getParametro(),
                exception.getValor(),
                exception.getMensagemUsuario(),
                contexto);
    }

    /**
     * Loga erro de validação específico
     *
     * @param logger Logger a ser usado
     * @param parametro Parâmetro que falhou na validação
     * @param valor Valor que causou a falha
     * @param motivo Motivo da falha
     */
    public static void logErroValidacao(Logger logger, String parametro, String valor, String motivo) {
        logger.warn("VALIDAÇÃO | Parâmetro: {} | Valor: '{}' | Motivo: {}", parametro, valor, motivo);
    }

    /**
     * Loga erro interno com stack trace resumido
     *
     * @param logger Logger a ser usado
     * @param operacao Operação que causou o erro
     * @param exception Exceção original
     * @param parametros Parâmetros relacionados (opcional)
     */
    public static void logErroInterno(Logger logger, String operacao, Exception exception, String parametros) {
        String params = parametros != null ? " | Parâmetros: " + parametros : "";
        logger.error("ERRO_INTERNO | Operação: {} | Tipo: {} | Mensagem: {}{}",
                operacao,
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                params,
                exception);
    }

    // ================================================
    // LOGS DE PERFORMANCE E AUDITORIA
    // ================================================

    /**
     * Loga informações de performance de operações
     *
     * @param logger Logger a ser usado
     * @param operacao Nome da operação
     * @param tempoExecucao Tempo de execução em millisegundos
     * @param resultados Quantidade de resultados processados
     */
    public static void logPerformance(Logger logger, String operacao, long tempoExecucao, int resultados) {
        logger.info("PERFORMANCE | Operação: {} | Tempo: {}ms | Resultados: {}",
                operacao, tempoExecucao, resultados);
    }

    /**
     * Loga tentativa de acesso não autorizado ou suspeito
     *
     * @param logger Logger a ser usado
     * @param operacao Operação tentada
     * @param parametro Parâmetro usado
     * @param motivo Motivo da suspeita
     */
    public static void logTentativaSuspeita(Logger logger, String operacao, String parametro, String motivo) {
        logger.warn("SEGURANÇA | Tentativa suspeita - Operação: {} | Parâmetro: {} | Motivo: {}",
                operacao, parametro, motivo);
    }

    // ================================================
    // LOGS DE CACHE E OTIMIZAÇÃO
    // ================================================

    /**
     * Loga hit/miss de cache
     *
     * @param logger Logger a ser usado
     * @param chaveCache Chave do cache
     * @param hit true para hit, false para miss
     */
    public static void logCache(Logger logger, String chaveCache, boolean hit) {
        if (hit) {
            logger.debug("CACHE | HIT para chave: {}", chaveCache);
        } else {
            logger.debug("CACHE | MISS para chave: {}", chaveCache);
        }
    }

    /**
     * Loga limpeza ou invalidação de cache
     *
     * @param logger Logger a ser usado
     * @param operacao Tipo de operação (limpeza, invalidação, etc.)
     * @param detalhes Detalhes da operação
     */
    public static void logCacheOperacao(Logger logger, String operacao, String detalhes) {
        logger.info("CACHE | Operação: {} | Detalhes: {}", operacao, detalhes);
    }

    // ================================================
    // LOGS DE SISTEMA
    // ================================================

    /**
     * Loga inicialização de componentes
     *
     * @param logger Logger a ser usado
     * @param componente Nome do componente
     * @param status Status da inicialização
     */
    public static void logInicializacao(Logger logger, String componente, String status) {
        logger.info("SISTEMA | Inicialização do {} - Status: {}", componente, status);
    }

    /**
     * Loga shutdown de componentes
     *
     * @param logger Logger a ser usado
     * @param componente Nome do componente
     * @param detalhes Detalhes do shutdown
     */
    public static void logShutdown(Logger logger, String componente, String detalhes) {
        logger.info("SISTEMA | Shutdown do {} - Detalhes: {}", componente, detalhes);
    }

    // ================================================
    // LOGS DE INTEGRAÇÃO
    // ================================================

    /**
     * Loga chamadas para sistemas externos
     *
     * @param logger Logger a ser usado
     * @param sistemaExterno Nome do sistema
     * @param operacao Operação realizada
     * @param tempoResposta Tempo de resposta em ms
     * @param sucesso true se bem-sucedida
     */
    public static void logIntegracao(Logger logger, String sistemaExterno, String operacao,
                                     long tempoResposta, boolean sucesso) {
        String status = sucesso ? "SUCESSO" : "FALHA";
        logger.info("INTEGRAÇÃO | Sistema: {} | Operação: {} | Tempo: {}ms | Status: {}",
                sistemaExterno, operacao, tempoResposta, status);
    }

    // ================================================
    // MÉTODOS DE CONVENIÊNCIA
    // ================================================

    /**
     * Cria uma mensagem padronizada para logs com múltiplos parâmetros
     *
     * @param prefixo Prefixo da mensagem
     * @param parametros Array de pares chave-valor
     * @return String formatada
     */
    public static String formatarMensagem(String prefixo, String... parametros) {
        StringBuilder sb = new StringBuilder(prefixo);

        for (int i = 0; i < parametros.length - 1; i += 2) {
            sb.append(" | ").append(parametros[i]).append(": ").append(parametros[i + 1]);
        }

        return sb.toString();
    }

    /**
     * Determina o nível de log apropriado baseado na severidade da exceção
     *
     * @param exception Exceção para avaliar
     * @return true se deve usar ERROR, false se deve usar WARN
     */
    public static boolean deveUsarLogError(CreditoExceptionBase exception) {
        if (exception == null) {
            return true;
        }

        // Erros de validação e não encontrado são WARN
        // Erros internos são ERROR
        return "ERRO_INTERNO".equals(exception.getTipoErro());
    }
}