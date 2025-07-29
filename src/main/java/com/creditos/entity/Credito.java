package com.creditos.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade que representa um Crédito Constituído de ISSQN

 *
 * @Entity
 * public class Credito {
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *     private String numeroCredito;
 *     private String numeroNfse;
 *     private LocalDate dataConstituicao;
 *     private BigDecimal valorIssqn;
 *     private String tipoCredito;
 *     private boolean simplesNacional;
 *     private BigDecimal aliquota;
 *     private BigDecimal valorFaturado;
 *     private BigDecimal valorDeducao;
 *     private BigDecimal baseCalculo;
 * }
 *
 * @author Ednilton Curt Rauh
 * @version 1.0.0
 */
@Entity
@Table(name = "credito", indexes = {
        @Index(name = "idx_credito_numero_nfse", columnList = "numero_nfse"),
        @Index(name = "idx_credito_numero_credito", columnList = "numero_credito")
})
public class Credito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_credito", nullable = false, length = 50)
    @NotBlank(message = "Número do crédito é obrigatório")
    @Size(max = 50, message = "Número do crédito deve ter no máximo 50 caracteres")
    private String numeroCredito;

    @Column(name = "numero_nfse", nullable = false, length = 50)
    @NotBlank(message = "Número da NFS-e é obrigatório")
    @Size(max = 50, message = "Número da NFS-e deve ter no máximo 50 caracteres")
    private String numeroNfse;

    @Column(name = "data_constituicao", nullable = false)
    @NotNull(message = "Data de constituição é obrigatória")
    private LocalDate dataConstituicao;

    @Column(name = "valor_issqn", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor do ISSQN é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor do ISSQN deve ser maior que zero")
    private BigDecimal valorIssqn;

    @Column(name = "tipo_credito", nullable = false, length = 50)
    @NotBlank(message = "Tipo do crédito é obrigatório")
    @Size(max = 50, message = "Tipo do crédito deve ter no máximo 50 caracteres")
    private String tipoCredito;

    @Column(name = "simples_nacional", nullable = false)
    @NotNull(message = "Informação sobre Simples Nacional é obrigatória")
    private Boolean simplesNacional;

    @Column(name = "aliquota", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Alíquota é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Alíquota deve ser maior que zero")
    @DecimalMax(value = "100.0", message = "Alíquota deve ser menor ou igual a 100")
    private BigDecimal aliquota;

    @Column(name = "valor_faturado", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor faturado é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor faturado deve ser maior que zero")
    private BigDecimal valorFaturado;

    @Column(name = "valor_deducao", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor de dedução é obrigatório")
    @DecimalMin(value = "0.0", message = "Valor de dedução deve ser maior ou igual a zero")
    private BigDecimal valorDeducao;

    @Column(name = "base_calculo", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Base de cálculo é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base de cálculo deve ser maior que zero")
    private BigDecimal baseCalculo;

    // ================================================
    // CONSTRUTORES
    // ================================================

    /**
     * Construtor padrão (obrigatório para JPA)
     */
    public Credito() {}

    /**
     * Construtor completo
     */
    public Credito(String numeroCredito, String numeroNfse, LocalDate dataConstituicao,
                   BigDecimal valorIssqn, String tipoCredito, Boolean simplesNacional,
                   BigDecimal aliquota, BigDecimal valorFaturado, BigDecimal valorDeducao,
                   BigDecimal baseCalculo) {
        this.numeroCredito = numeroCredito;
        this.numeroNfse = numeroNfse;
        this.dataConstituicao = dataConstituicao;
        this.valorIssqn = valorIssqn;
        this.tipoCredito = tipoCredito;
        this.simplesNacional = simplesNacional;
        this.aliquota = aliquota;
        this.valorFaturado = valorFaturado;
        this.valorDeducao = valorDeducao;
        this.baseCalculo = baseCalculo;
    }

    // ================================================
    // GETTERS E SETTERS
    // ================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
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
        Credito credito = (Credito) o;
        return Objects.equals(id, credito.id) &&
                Objects.equals(numeroCredito, credito.numeroCredito) &&
                Objects.equals(numeroNfse, credito.numeroNfse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroCredito, numeroNfse);
    }

    @Override
    public String toString() {
        return "Credito{" +
                "id=" + id +
                ", numeroCredito='" + numeroCredito + '\'' +
                ", numeroNfse='" + numeroNfse + '\'' +
                ", dataConstituicao=" + dataConstituicao +
                ", valorIssqn=" + valorIssqn +
                ", tipoCredito='" + tipoCredito + '\'' +
                ", simplesNacional=" + simplesNacional +
                ", aliquota=" + aliquota +
                ", valorFaturado=" + valorFaturado +
                ", valorDeducao=" + valorDeducao +
                ", baseCalculo=" + baseCalculo +
                '}';
    }

    // ================================================
    // MÉTODOS DE NEGÓCIO (OPCIONAIS)
    // ================================================

    /**
     * Calcula se o valor do ISSQN está correto com base na alíquota e base de cálculo
     */
    public boolean isValorIssqnCorreto() {
        if (aliquota == null || baseCalculo == null || valorIssqn == null) {
            return false;
        }

        BigDecimal valorCalculado = baseCalculo
                .multiply(aliquota)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        return valorCalculado.compareTo(valorIssqn) == 0;
    }

    /**
     * Retorna descrição do Simples Nacional
     */
    public String getSimplesNacionalDescricao() {
        return simplesNacional != null && simplesNacional ? "Sim" : "Não";
    }

    /**
     * Verifica se é um crédito de ISSQN
     */
    public boolean isIssqn() {
        return "ISSQN".equalsIgnoreCase(tipoCredito);
    }
}