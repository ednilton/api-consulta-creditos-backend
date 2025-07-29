package com.creditos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO de resposta para consulta de créditos
 * Seguindo EXATAMENTE o JSON especificado no PDF do desafio técnico
 *
 * @author Ednilton Curt Rauh
 * @version 1.0.0
 */
public class CreditoResponseDTO {

    @JsonProperty("numeroCredito")
    @NotBlank(message = "Número do crédito é obrigatório")
    private String numeroCredito;

    @JsonProperty("numeroNfse")
    @NotBlank(message = "Número da NFS-e é obrigatório")
    private String numeroNfse;

    @JsonProperty("dataConstituicao")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Data de constituição é obrigatória")
    private LocalDate dataConstituicao;

    @JsonProperty("valorIssqn")
    @NotNull(message = "Valor do ISSQN é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor do ISSQN deve ser maior que zero")
    private BigDecimal valorIssqn;

    @JsonProperty("tipoCredito")
    @NotBlank(message = "Tipo do crédito é obrigatório")
    private String tipoCredito;

    @JsonProperty("simplesNacional")
    @NotNull(message = "Informação sobre Simples Nacional é obrigatória")
    private String simplesNacional; // "Sim" ou "Não" conforme PDF

    @JsonProperty("aliquota")
    @NotNull(message = "Alíquota é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Alíquota deve ser maior que zero")
    private BigDecimal aliquota;

    @JsonProperty("valorFaturado")
    @NotNull(message = "Valor faturado é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor faturado deve ser maior que zero")
    private BigDecimal valorFaturado;

    @JsonProperty("valorDeducao")
    @NotNull(message = "Valor de dedução é obrigatório")
    @DecimalMin(value = "0.0", message = "Valor de dedução deve ser maior ou igual a zero")
    private BigDecimal valorDeducao;

    @JsonProperty("baseCalculo")
    @NotNull(message = "Base de cálculo é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base de cálculo deve ser maior que zero")
    private BigDecimal baseCalculo;

    // ================================================
    // CONSTRUTORES
    // ================================================

    /**
     * Construtor padrão (obrigatório para Jackson)
     */
    public CreditoResponseDTO() {}

    /**
     * Construtor completo para conversão de entidade
     */
    public CreditoResponseDTO(String numeroCredito, String numeroNfse, LocalDate dataConstituicao,
                              BigDecimal valorIssqn, String tipoCredito, Boolean simplesNacional,
                              BigDecimal aliquota, BigDecimal valorFaturado, BigDecimal valorDeducao,
                              BigDecimal baseCalculo) {
        this.numeroCredito = numeroCredito;
        this.numeroNfse = numeroNfse;
        this.dataConstituicao = dataConstituicao;
        this.valorIssqn = valorIssqn;
        this.tipoCredito = tipoCredito;
        this.simplesNacional = simplesNacional != null && simplesNacional ? "Sim" : "Não";
        this.aliquota = aliquota;
        this.valorFaturado = valorFaturado;
        this.valorDeducao = valorDeducao;
        this.baseCalculo = baseCalculo;
    }

    // ================================================
    // GETTERS E SETTERS
    // ================================================

    public String getNumeroCredito() {
        return numeroCredito;
    }

    public void setNumeroCredito(String numeroCredito) {
        this.numeroCredito = numeroCredito;
    }

    public String getNumeroNfse() {
        return numeroNfse;
    }

    public void setNumeroNfse(String numeroNfse) {
        this.numeroNfse = numeroNfse;
    }

    public LocalDate getDataConstituicao() {
        return dataConstituicao;
    }

    public void setDataConstituicao(LocalDate dataConstituicao) {
        this.dataConstituicao = dataConstituicao;
    }

    public BigDecimal getValorIssqn() {
        return valorIssqn;
    }

    public void setValorIssqn(BigDecimal valorIssqn) {
        this.valorIssqn = valorIssqn;
    }

    public String getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(String tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public String getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(String simplesNacional) {
        this.simplesNacional = simplesNacional;
    }

    /**
     * Setter auxiliar para converter Boolean em String
     */
    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional != null && simplesNacional ? "Sim" : "Não";
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorFaturado() {
        return valorFaturado;
    }

    public void setValorFaturado(BigDecimal valorFaturado) {
        this.valorFaturado = valorFaturado;
    }

    public BigDecimal getValorDeducao() {
        return valorDeducao;
    }

    public void setValorDeducao(BigDecimal valorDeducao) {
        this.valorDeducao = valorDeducao;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    // ================================================
    // MÉTODOS EQUALS, HASHCODE E TOSTRING
    // ================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditoResponseDTO that = (CreditoResponseDTO) o;
        return Objects.equals(numeroCredito, that.numeroCredito) &&
                Objects.equals(numeroNfse, that.numeroNfse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCredito, numeroNfse);
    }

    @Override
    public String toString() {
        return "CreditoResponseDTO{" +
                "numeroCredito='" + numeroCredito + '\'' +
                ", numeroNfse='" + numeroNfse + '\'' +
                ", dataConstituicao=" + dataConstituicao +
                ", valorIssqn=" + valorIssqn +
                ", tipoCredito='" + tipoCredito + '\'' +
                ", simplesNacional='" + simplesNacional + '\'' +
                ", aliquota=" + aliquota +
                ", valorFaturado=" + valorFaturado +
                ", valorDeducao=" + valorDeducao +
                ", baseCalculo=" + baseCalculo +
                '}';
    }
}