package com.creditos.util;

import com.creditos.exception.CreditoException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe utilitária para validações comuns
 * Implementa o princípio DRY evitando duplicação de código de validação
 * Atualizada para usar o novo sistema de exceções CreditoException
 *
 * @author Ednilton Curt Rauh
 * @version 2.1.0
 */
public class ValidationUtils {

    // Padrões regex para validação
    private static final Pattern NUMERO_CREDITO_PATTERN = Pattern.compile("^[0-9]{1,50}$");
    private static final Pattern NUMERO_NFSE_PATTERN = Pattern.compile("^[0-9]{1,50}$");

    private ValidationUtils() {
        // Construtor privado para classe utilitária
    }

    // ================================================
    // VALIDAÇÕES BÁSICAS
    // ================================================

    /**
     * Valida se o número da NFS-e é válido
     *
     * @param numeroNfse Número da NFS-e a ser validado
     * @throws CreditoException se o número for inválido
     */
    public static void validateNumeroNfse(String numeroNfse) {
        if (numeroNfse == null || numeroNfse.trim().isEmpty()) {
            throw CreditoException.numeroNfseInvalido(numeroNfse, "é obrigatório");
        }

        String numeroLimpo = numeroNfse.trim();
        if (numeroLimpo.length() > 50) {
            throw CreditoException.numeroNfseInvalido(numeroNfse, "deve ter no máximo 50 caracteres");
        }

        if (!NUMERO_NFSE_PATTERN.matcher(numeroLimpo).matches()) {
            throw CreditoException.numeroNfseInvalido(numeroNfse, "deve conter apenas dígitos");
        }
    }

    /**
     * Valida se o número do crédito é válido
     *
     * @param numeroCredito Número do crédito a ser validado
     * @throws CreditoException se o número for inválido
     */
    public static void validateNumeroCredito(String numeroCredito) {
        if (numeroCredito == null || numeroCredito.trim().isEmpty()) {
            throw CreditoException.numeroCreditoInvalido(numeroCredito, "é obrigatório");
        }

        String numeroLimpo = numeroCredito.trim();
        if (numeroLimpo.length() > 50) {
            throw CreditoException.numeroCreditoInvalido(numeroCredito, "deve ter no máximo 50 caracteres");
        }

        if (!NUMERO_CREDITO_PATTERN.matcher(numeroLimpo).matches()) {
            throw CreditoException.numeroCreditoInvalido(numeroCredito, "deve conter apenas números");
        }
    }

    // ================================================
    // VALIDAÇÕES DE VALORES
    // ================================================

    /**
     * Valida valor monetário
     *
     * @param valor Valor a ser validado
     * @param nomeParametro Nome do parâmetro para mensagem de erro
     * @throws CreditoException se o valor for inválido
     */
    public static void validateValorMonetario(BigDecimal valor, String nomeParametro) {
        if (valor == null) {
            throw CreditoException.erroInterno(nomeParametro + " é obrigatório");
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw CreditoException.erroInterno(nomeParametro + " não pode ser negativo");
        }

        // Validar precisão (máximo 2 casas decimais)
        if (valor.scale() > 2) {
            throw CreditoException.erroInterno(nomeParametro + " deve ter no máximo 2 casas decimais");
        }
    }

    /**
     * Valida valor monetário positivo
     *
     * @param valor Valor a ser validado
     * @param nomeParametro Nome do parâmetro para mensagem de erro
     * @throws CreditoException se o valor for inválido
     */
    public static void validateValorMonetarioPositivo(BigDecimal valor, String nomeParametro) {
        validateValorMonetario(valor, nomeParametro);

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw CreditoException.erroInterno(nomeParametro + " deve ser maior que zero");
        }
    }

    /**
     * Valida alíquota (0 a 100)
     *
     * @param aliquota Alíquota a ser validada
     * @throws CreditoException se a alíquota for inválida
     */
    public static void validateAliquota(BigDecimal aliquota) {
        if (aliquota == null) {
            throw CreditoException.erroInterno("Alíquota é obrigatória");
        }

        if (aliquota.compareTo(BigDecimal.ZERO) < 0 ||
                aliquota.compareTo(new BigDecimal("100")) > 0) {
            throw CreditoException.erroInterno("Alíquota deve estar entre 0 e 100");
        }
    }

    // ================================================
    // VALIDAÇÕES DE DATA
    // ================================================

    /**
     * Valida data obrigatória
     *
     * @param data Data a ser validada
     * @param nomeParametro Nome do parâmetro para mensagem de erro
     * @throws CreditoException se a data for inválida
     */
    public static void validateDataObrigatoria(LocalDate data, String nomeParametro) {
        if (data == null) {
            throw CreditoException.erroInterno(nomeParametro + " é obrigatório");
        }
    }

    /**
     * Valida período de datas
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @throws CreditoException se o período for inválido
     */
    public static void validatePeriodo(LocalDate dataInicio, LocalDate dataFim) {
        validateDataObrigatoria(dataInicio, "Data inicial");
        validateDataObrigatoria(dataFim, "Data final");

        if (dataInicio.isAfter(dataFim)) {
            throw CreditoException.erroInterno("Data inicial deve ser anterior ou igual à data final");
        }

        // Validar se o período não é muito extenso (por exemplo, máximo 5 anos)
        if (dataInicio.plusYears(5).isBefore(dataFim)) {
            throw CreditoException.erroInterno("Período não pode exceder 5 anos");
        }
    }

    // ================================================
    // VALIDAÇÕES DE REGRAS DE NEGÓCIO
    // ================================================

    /**
     * Valida consistência dos valores de ISSQN
     *
     * @param valorIssqn Valor do ISSQN
     * @param baseCalculo Base de cálculo
     * @param aliquota Alíquota aplicada
     * @throws CreditoException se os valores forem inconsistentes
     */
    public static void validateConsistenciaIssqn(BigDecimal valorIssqn, BigDecimal baseCalculo, BigDecimal aliquota) {
        if (valorIssqn == null || baseCalculo == null || aliquota == null) {
            return; // Validações individuais devem ser feitas antes
        }

        BigDecimal valorCalculado = baseCalculo
                .multiply(aliquota)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        // Tolerância de 1 centavo para diferenças de arredondamento
        BigDecimal diferenca = valorIssqn.subtract(valorCalculado).abs();
        if (diferenca.compareTo(new BigDecimal("0.01")) > 0) {
            throw CreditoException.erroInterno(
                    String.format("Valor ISSQN (%.2f) inconsistente com cálculo: %.2f * %.2f%% = %.2f",
                            valorIssqn, baseCalculo, aliquota, valorCalculado)
            );
        }
    }

    /**
     * Valida tipo de crédito
     *
     * @param tipoCredito Tipo do crédito
     * @throws CreditoException se o tipo for inválido
     */
    public static void validateTipoCredito(String tipoCredito) {
        if (tipoCredito == null || tipoCredito.trim().isEmpty()) {
            throw CreditoException.erroInterno("Tipo de crédito é obrigatório");
        }

        String tipoLimpo = tipoCredito.trim().toUpperCase();
        List<String> tiposValidos = Arrays.asList("ISSQN", "IPTU", "ITBI", "TAXAS");

        if (!tiposValidos.contains(tipoLimpo)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tiposValidos.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(tiposValidos.get(i));
            }
            throw CreditoException.erroInterno("Tipos válidos: " + sb.toString());
        }
    }

    // ================================================
    // VALIDAÇÕES COMBINADAS
    // ================================================

    /**
     * Valida todos os dados obrigatórios de um crédito
     *
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param valorIssqn Valor do ISSQN
     * @param tipoCredito Tipo do crédito
     * @param aliquota Alíquota
     * @param valorFaturado Valor faturado
     * @param baseCalculo Base de cálculo
     * @throws CreditoException se houver erros
     */
    public static void validateDadosCredito(String numeroCredito, String numeroNfse,
                                            LocalDate dataConstituicao, BigDecimal valorIssqn,
                                            String tipoCredito, BigDecimal aliquota,
                                            BigDecimal valorFaturado, BigDecimal baseCalculo) {

        List<String> erros = new ArrayList<>();

        try {
            validateNumeroCredito(numeroCredito);
        } catch (CreditoException e) {
            erros.add("Número do crédito: " + e.getMensagemUsuario());
        }

        try {
            validateNumeroNfse(numeroNfse);
        } catch (CreditoException e) {
            erros.add("Número da NFS-e: " + e.getMensagemUsuario());
        }

        try {
            validateDataObrigatoria(dataConstituicao, "dataConstituicao");
        } catch (CreditoException e) {
            erros.add("Data de constituição: " + e.getMensagemUsuario());
        }

        try {
            validateValorMonetarioPositivo(valorIssqn, "valorIssqn");
        } catch (CreditoException e) {
            erros.add("Valor ISSQN: " + e.getMensagemUsuario());
        }

        try {
            validateTipoCredito(tipoCredito);
        } catch (CreditoException e) {
            erros.add("Tipo de crédito: " + e.getMensagemUsuario());
        }

        try {
            validateAliquota(aliquota);
        } catch (CreditoException e) {
            erros.add("Alíquota: " + e.getMensagemUsuario());
        }

        try {
            validateValorMonetarioPositivo(valorFaturado, "valorFaturado");
        } catch (CreditoException e) {
            erros.add("Valor faturado: " + e.getMensagemUsuario());
        }

        try {
            validateValorMonetarioPositivo(baseCalculo, "baseCalculo");
        } catch (CreditoException e) {
            erros.add("Base de cálculo: " + e.getMensagemUsuario());
        }

        // Se há erros básicos, lançar exceção
        if (!erros.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < erros.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(erros.get(i));
            }
            throw CreditoException.erroInterno("Múltiplas violações encontradas: " + sb.toString());
        }

        // Validar consistência dos valores
        try {
            validateConsistenciaIssqn(valorIssqn, baseCalculo, aliquota);
        } catch (CreditoException e) {
            erros.add("Consistência: " + e.getMensagemUsuario());
        }

        if (!erros.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < erros.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(erros.get(i));
            }
            throw CreditoException.erroInterno("Inconsistências encontradas: " + sb.toString());
        }
    }

    // ================================================
    // MÉTODOS UTILITÁRIOS
    // ================================================

    /**
     * Normaliza uma string removendo espaços extras
     *
     * @param value String a ser normalizada
     * @return String normalizada ou null se a entrada for null
     */
    public static String normalizeString(String value) {
        return value != null ? value.trim() : null;
    }

    /**
     * Verifica se uma string é válida (não nula e não vazia após trim)
     *
     * @param value String a ser verificada
     * @return true se a string for válida, false caso contrário
     */
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida string com tamanho mínimo e máximo
     *
     * @param value String a ser validada
     * @param nomeParametro Nome do parâmetro
     * @param tamanhoMinimo Tamanho mínimo
     * @param tamanhoMaximo Tamanho máximo
     * @throws CreditoException se inválida
     */
    public static void validateStringLength(String value, String nomeParametro,
                                            int tamanhoMinimo, int tamanhoMaximo) {
        if (value == null || value.trim().isEmpty()) {
            throw CreditoException.erroInterno(nomeParametro + " é obrigatório");
        }

        String valorLimpo = value.trim();
        if (valorLimpo.length() < tamanhoMinimo || valorLimpo.length() > tamanhoMaximo) {
            throw CreditoException.erroInterno(
                    String.format("%s deve ter entre %d e %d caracteres", nomeParametro, tamanhoMinimo, tamanhoMaximo)
            );
        }
    }

    // ================================================
    // MÉTODOS ESPECÍFICOS PARA NÚMEROS DE CRÉDITO E NFS-E
    // ================================================

    /**
     * Validação simplificada apenas para verificar se número de crédito tem formato básico válido
     * (usado em operações que não devem lançar exceção, como exists)
     *
     * @param numeroCredito Número do crédito
     * @return true se formato básico for válido, false caso contrário
     */
    public static boolean isValidNumeroCredito(String numeroCredito) {
        if (numeroCredito == null || numeroCredito.trim().isEmpty()) {
            return false;
        }

        String numeroLimpo = numeroCredito.trim();
        return numeroLimpo.length() <= 50 && NUMERO_CREDITO_PATTERN.matcher(numeroLimpo).matches();
    }

    /**
     * Validação simplificada apenas para verificar se número de NFS-e tem formato básico válido
     * (usado em operações que não devem lançar exceção, como exists)
     *
     * @param numeroNfse Número da NFS-e
     * @return true se formato básico for válido, false caso contrário
     */
    public static boolean isValidNumeroNfse(String numeroNfse) {
        if (numeroNfse == null || numeroNfse.trim().isEmpty()) {
            return false;
        }

        String numeroLimpo = numeroNfse.trim();
        return numeroLimpo.length() <= 50 && NUMERO_NFSE_PATTERN.matcher(numeroLimpo).matches();
    }
}