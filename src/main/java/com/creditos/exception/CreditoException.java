package com.creditos.exception;

/**
 * Classe geral para lançar todos os tipos de erros do sistema de créditos
 * Centraliza a criação de exceções com factory methods simples
 */
public class CreditoException extends CreditoExceptionBase {

    private final int httpStatus;

    private CreditoException(String codigoErro, String tipoErro, String mensagemUsuario,
                             String parametro, String valor, int httpStatus) {
        super(codigoErro, tipoErro, mensagemUsuario, parametro, valor);
        this.httpStatus = httpStatus;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Crédito não encontrado
     */
    public static CreditoException creditoNaoEncontrado(String numeroCredito) {
        return new CreditoException(
                "CRED_001",
                "CREDITO_NAO_ENCONTRADO",
                "Crédito ISSQN não localizado no sistema",
                "numeroCredito",
                numeroCredito,
                404
        );
    }

    /**
     * NFS-e sem créditos
     */
    public static CreditoException nfseSemCreditos(String numeroNfse) {
        return new CreditoException(
                "NFSE_001",
                "NFSE_SEM_CREDITOS",
                "Não foram encontrados créditos constituídos para esta NFS-e",
                "numeroNfse",
                numeroNfse,
                404
        );
    }

    /**
     * Número de crédito inválido
     */
    public static CreditoException numeroCreditoInvalido(String numeroCredito, String motivo) {
        return new CreditoException(
                "PARAM_001",
                "PARAMETRO_INVALIDO",
                "Número do crédito informado é inválido: " + motivo,
                "numeroCredito",
                numeroCredito,
                400
        );
    }

    /**
     * Número de NFS-e inválido
     */
    public static CreditoException numeroNfseInvalido(String numeroNfse, String motivo) {
        return new CreditoException(
                "PARAM_002",
                "PARAMETRO_INVALIDO",
                "Número da NFS-e informado é inválido: " + motivo,
                "numeroNfse",
                numeroNfse,
                400
        );
    }

    /**
     * Erro interno do sistema
     */
    public static CreditoException erroInterno(String detalhes) {
        return new CreditoException(
                "SYS_001",
                "ERRO_INTERNO",
                "Erro interno na consulta de créditos. Tente novamente em alguns instantes",
                null,
                detalhes,
                500
        );
    }
}