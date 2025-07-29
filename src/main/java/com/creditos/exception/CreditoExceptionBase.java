package com.creditos.exception;

import java.time.LocalDateTime;

/**
 * Classe abstrata base para todas as exceções do sistema de créditos ISSQN
 * Fornece estrutura comum e comportamentos básicos
 */
public abstract class CreditoExceptionBase extends RuntimeException {

    private final String codigoErro;
    private final String tipoErro;
    private final String mensagemUsuario;
    private final String parametro;
    private final String valor;
    private final LocalDateTime timestamp;

    protected CreditoExceptionBase(String codigoErro, String tipoErro, String mensagemUsuario,
                                   String parametro, String valor) {
        super(mensagemUsuario);
        this.codigoErro = codigoErro;
        this.tipoErro = tipoErro;
        this.mensagemUsuario = mensagemUsuario;
        this.parametro = parametro;
        this.valor = valor;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getCodigoErro() { return codigoErro; }
    public String getTipoErro() { return tipoErro; }
    public String getMensagemUsuario() { return mensagemUsuario; }
    public String getParametro() { return parametro; }
    public String getValor() { return valor; }
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Retorna o HTTP status code apropriado para cada tipo de erro
     */
    public abstract int getHttpStatus();

    /**
     * Indica se deve gerar log para este erro
     */
    public boolean deveLogar() {
        return true;
    }
}