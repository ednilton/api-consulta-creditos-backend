package com.creditos.repository;

import com.creditos.entity.Credito;
import com.creditos.service.CreditoService.EstatisticasPorTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository refatorado para operações de dados da entidade Credito
 * Usando JPQL puro para máxima compatibilidade com PostgreSQL e Java 8
 * Corrigidos conflitos de métodos e queries problemáticas
 *
 * @author Ednilton Curt Rauh
 * @version 2.1.0
 */
@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {

    // ================================================
    // CONSULTAS PRINCIPAIS (CONFORME ESPECIFICAÇÃO)
    // ================================================

    /**
     * Busca créditos pelo número da NFS-e
     * Endpoint: GET /api/creditos/{numeroNfse}
     *
     * @param numeroNfse Número da NFS-e
     * @return Lista de créditos encontrados ordenados por data de constituição
     */
    @Query("SELECT c FROM Credito c WHERE c.numeroNfse = :numeroNfse ORDER BY c.dataConstituicao DESC")
    List<Credito> findByNumeroNfse(@Param("numeroNfse") String numeroNfse);

    /**
     * Busca créditos pelo número do crédito (pode retornar múltiplos se houver duplicatas)
     * Endpoint: GET /api/creditos/credito/{numeroCredito}
     *
     * @param numeroCredito Número do crédito constituído
     * @return Lista de créditos com o número especificado (ordenados por ID)
     */
    @Query("SELECT c FROM Credito c WHERE c.numeroCredito = :numeroCredito ORDER BY c.id ASC")
    List<Credito> findByNumeroCredito(@Param("numeroCredito") String numeroCredito);

    /**
     * Busca um único crédito pelo número (para casos onde há garantia de unicidade)
     *
     * @param numeroCredito Número do crédito constituído
     * @return Optional contendo o crédito se encontrado
     */
    @Query("SELECT c FROM Credito c WHERE c.numeroCredito = :numeroCredito ORDER BY c.id ASC")
    Optional<Credito> findFirstByNumeroCredito(@Param("numeroCredito") String numeroCredito);

    // ================================================
    // CONSULTAS POR PERÍODO
    // ================================================

    /**
     * Busca créditos por período de constituição
     *
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de créditos no período
     */
    @Query("SELECT c FROM Credito c WHERE c.dataConstituicao BETWEEN :dataInicio AND :dataFim ORDER BY c.dataConstituicao DESC")
    List<Credito> findByDataConstituicaoBetween(@Param("dataInicio") LocalDate dataInicio,
                                                @Param("dataFim") LocalDate dataFim);

    /**
     * Busca créditos por período com paginação
     *
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @param pageable Informações de paginação
     * @return Página de créditos no período
     */
    @Query("SELECT c FROM Credito c WHERE c.dataConstituicao BETWEEN :dataInicio AND :dataFim ORDER BY c.dataConstituicao DESC")
    Page<Credito> findByDataConstituicaoBetweenPaginated(@Param("dataInicio") LocalDate dataInicio,
                                                         @Param("dataFim") LocalDate dataFim,
                                                         Pageable pageable);

    /**
     * Busca créditos constituídos em uma data específica
     *
     * @param dataConstituicao Data de constituição
     * @return Lista de créditos da data
     */
    @Query("SELECT c FROM Credito c WHERE c.dataConstituicao = :dataConstituicao ORDER BY c.numeroCredito")
    List<Credito> findByDataConstituicao(@Param("dataConstituicao") LocalDate dataConstituicao);

    /**
     * Busca créditos constituídos após uma data
     *
     * @param dataInicio Data inicial
     * @return Lista de créditos posteriores à data
     */
    @Query("SELECT c FROM Credito c WHERE c.dataConstituicao >= :dataInicio ORDER BY c.dataConstituicao DESC")
    List<Credito> findByDataConstituicaoAfter(@Param("dataInicio") LocalDate dataInicio);

    // ================================================
    // CONSULTAS POR CARACTERÍSTICAS
    // ================================================

    /**
     * Busca créditos por tipo (case insensitive)
     *
     * @param tipoCredito Tipo do crédito
     * @return Lista de créditos do tipo especificado
     */
    @Query("SELECT c FROM Credito c WHERE UPPER(c.tipoCredito) = UPPER(:tipoCredito) ORDER BY c.dataConstituicao DESC")
    List<Credito> findByTipoCreditoIgnoreCase(@Param("tipoCredito") String tipoCredito);

    /**
     * Busca créditos por tipo com paginação
     *
     * @param tipoCredito Tipo do crédito
     * @param pageable Informações de paginação
     * @return Página de créditos do tipo especificado
     */
    @Query("SELECT c FROM Credito c WHERE UPPER(c.tipoCredito) = UPPER(:tipoCredito) ORDER BY c.dataConstituicao DESC")
    Page<Credito> findByTipoCreditoPaginated(@Param("tipoCredito") String tipoCredito, Pageable pageable);

    /**
     * Busca créditos por status do Simples Nacional
     *
     * @param simplesNacional true para optantes, false para não optantes
     * @return Lista de créditos filtrados
     */
    @Query("SELECT c FROM Credito c WHERE c.simplesNacional = :simplesNacional ORDER BY c.dataConstituicao DESC")
    List<Credito> findBySimplesNacional(@Param("simplesNacional") Boolean simplesNacional);

    /**
     * Busca créditos por Simples Nacional com paginação
     *
     * @param simplesNacional Status do Simples Nacional
     * @param pageable Informações de paginação
     * @return Página de créditos filtrados
     */
    @Query("SELECT c FROM Credito c WHERE c.simplesNacional = :simplesNacional ORDER BY c.dataConstituicao DESC")
    Page<Credito> findBySimplesNacionalPaginated(@Param("simplesNacional") Boolean simplesNacional, Pageable pageable);

    // ================================================
    // CONSULTAS POR VALORES
    // ================================================

    /**
     * Busca créditos por faixa de valor do ISSQN
     *
     * @param valorMinimo Valor mínimo
     * @param valorMaximo Valor máximo
     * @return Lista de créditos na faixa
     */
    @Query("SELECT c FROM Credito c WHERE c.valorIssqn BETWEEN :valorMinimo AND :valorMaximo ORDER BY c.valorIssqn DESC")
    List<Credito> findByValorIssqnBetween(@Param("valorMinimo") BigDecimal valorMinimo,
                                          @Param("valorMaximo") BigDecimal valorMaximo);

    /**
     * Busca créditos com valor do ISSQN maior que um valor específico
     *
     * @param valorMinimo Valor mínimo
     * @return Lista de créditos acima do valor
     */
    @Query("SELECT c FROM Credito c WHERE c.valorIssqn > :valorMinimo ORDER BY c.valorIssqn DESC")
    List<Credito> findByValorIssqnGreaterThan(@Param("valorMinimo") BigDecimal valorMinimo);

    /**
     * Busca créditos por faixa de alíquota
     *
     * @param aliquotaMinima Alíquota mínima
     * @param aliquotaMaxima Alíquota máxima
     * @return Lista de créditos na faixa de alíquota
     */
    @Query("SELECT c FROM Credito c WHERE c.aliquota BETWEEN :aliquotaMinima AND :aliquotaMaxima ORDER BY c.aliquota")
    List<Credito> findByAliquotaBetween(@Param("aliquotaMinima") BigDecimal aliquotaMinima,
                                        @Param("aliquotaMaxima") BigDecimal aliquotaMaxima);

    // ================================================
    // CONSULTAS COMBINADAS
    // ================================================

    /**
     * Busca créditos por tipo e período
     *
     * @param tipoCredito Tipo do crédito
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de créditos filtrados
     */
    @Query("SELECT c FROM Credito c WHERE UPPER(c.tipoCredito) = UPPER(:tipoCredito) " +
            "AND c.dataConstituicao BETWEEN :dataInicio AND :dataFim ORDER BY c.dataConstituicao DESC")
    List<Credito> findByTipoCreditoAndPeriodo(@Param("tipoCredito") String tipoCredito,
                                              @Param("dataInicio") LocalDate dataInicio,
                                              @Param("dataFim") LocalDate dataFim);

    /**
     * Busca créditos por Simples Nacional e período
     *
     * @param simplesNacional Status do Simples Nacional
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de créditos filtrados
     */
    @Query("SELECT c FROM Credito c WHERE c.simplesNacional = :simplesNacional " +
            "AND c.dataConstituicao BETWEEN :dataInicio AND :dataFim ORDER BY c.dataConstituicao DESC")
    List<Credito> findBySimplesNacionalAndPeriodo(@Param("simplesNacional") Boolean simplesNacional,
                                                  @Param("dataInicio") LocalDate dataInicio,
                                                  @Param("dataFim") LocalDate dataFim);

    // ================================================
    // CONSULTAS DE ESTATÍSTICAS
    // ================================================

    /**
     * Calcula o valor total de ISSQN constituído
     *
     * @return Soma total dos valores de ISSQN
     */
    @Query("SELECT SUM(c.valorIssqn) FROM Credito c")
    BigDecimal sumValorIssqn();

    /**
     * Calcula a média dos valores de ISSQN
     *
     * @return Valor médio do ISSQN
     */
    @Query("SELECT AVG(c.valorIssqn) FROM Credito c")
    BigDecimal avgValorIssqn();

    /**
     * Encontra o maior valor de ISSQN
     *
     * @return Maior valor de ISSQN
     */
    @Query("SELECT MAX(c.valorIssqn) FROM Credito c")
    BigDecimal maxValorIssqn();

    /**
     * Encontra o menor valor de ISSQN
     *
     * @return Menor valor de ISSQN
     */
    @Query("SELECT MIN(c.valorIssqn) FROM Credito c")
    BigDecimal minValorIssqn();

    /**
     * Conta créditos por tipo
     *
     * @param tipoCredito Tipo do crédito
     * @return Quantidade de créditos do tipo
     */
    @Query("SELECT COUNT(c) FROM Credito c WHERE UPPER(c.tipoCredito) = UPPER(:tipoCredito)")
    Long countByTipoCredito(@Param("tipoCredito") String tipoCredito);

    /**
     * Conta créditos do Simples Nacional
     *
     * @param simplesNacional Status do Simples Nacional
     * @return Quantidade de créditos
     */
    @Query("SELECT COUNT(c) FROM Credito c WHERE c.simplesNacional = :simplesNacional")
    Long countBySimplesNacional(@Param("simplesNacional") Boolean simplesNacional);

    /**
     * Calcula estatísticas agrupadas por tipo de crédito
     *
     * @return Lista de estatísticas por tipo
     */
    @Query("SELECT new com.creditos.service.CreditoService$EstatisticasPorTipo(" +
            "c.tipoCredito, COUNT(c), SUM(c.valorIssqn), AVG(c.valorIssqn)) " +
            "FROM Credito c GROUP BY c.tipoCredito ORDER BY c.tipoCredito")
    List<EstatisticasPorTipo> findEstatisticasPorTipo();

    /**
     * Calcula valor total por período
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Valor total no período
     */
    @Query("SELECT SUM(c.valorIssqn) FROM Credito c WHERE c.dataConstituicao BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumValorIssqnByPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                      @Param("dataFim") LocalDate dataFim);

    /**
     * Conta créditos por período
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Quantidade de créditos no período
     */
    @Query("SELECT COUNT(c) FROM Credito c WHERE c.dataConstituicao BETWEEN :dataInicio AND :dataFim")
    Long countByPeriodo(@Param("dataInicio") LocalDate dataInicio,
                        @Param("dataFim") LocalDate dataFim);

    // ================================================
    // CONSULTAS DE BUSCA TEXTUAL
    // ================================================

    /**
     * Busca créditos que contenham um texto no número da NFS-e
     *
     * @param numeroNfse Parte do número da NFS-e
     * @return Lista de créditos encontrados
     */
    @Query("SELECT c FROM Credito c WHERE c.numeroNfse LIKE CONCAT('%', :numeroNfse, '%') ORDER BY c.dataConstituicao DESC")
    List<Credito> findByNumeroNfseContaining(@Param("numeroNfse") String numeroNfse);

    /**
     * Busca créditos que contenham um texto no número do crédito
     *
     * @param numeroCredito Parte do número do crédito
     * @return Lista de créditos encontrados
     */
    @Query("SELECT c FROM Credito c WHERE c.numeroCredito LIKE CONCAT('%', :numeroCredito, '%') ORDER BY c.dataConstituicao DESC")
    List<Credito> findByNumeroCreditoContaining(@Param("numeroCredito") String numeroCredito);

    // ================================================
    // CONSULTAS DE VALIDAÇÃO
    // ================================================

    /**
     * Verifica se existe um crédito com número específico (diferente do ID atual)
     * Útil para validação de duplicatas em atualizações
     *
     * @param numeroCredito Número do crédito
     * @param id ID do crédito atual (para excluir da busca)
     * @return true se existir outro crédito com o mesmo número
     */
    @Query("SELECT COUNT(c) > 0 FROM Credito c WHERE c.numeroCredito = :numeroCredito AND c.id != :id")
    boolean existsByNumeroCreditoAndIdNot(@Param("numeroCredito") String numeroCredito, @Param("id") Long id);

    /**
     * Busca créditos com valores inconsistentes (para auditoria)
     * Verifica se o valor do ISSQN está correto baseado na alíquota e base de cálculo
     *
     * @return Lista de créditos com possíveis inconsistências
     */
    @Query("SELECT c FROM Credito c WHERE ABS(c.valorIssqn - (c.baseCalculo * c.aliquota / 100)) > 0.01")
    List<Credito> findCreditosComValoresInconsistentes();

    // ================================================
    // CONSULTAS PARA RELATÓRIOS
    // ================================================

    /**
     * Busca créditos ordenados por valor (do maior para o menor)
     *
     * @return Lista de créditos ordenados por valor
     */
    @Query("SELECT c FROM Credito c ORDER BY c.valorIssqn DESC")
    List<Credito> findAllOrderByValorIssqnDesc();

    /**
     * Busca os N maiores créditos por valor
     *
     * @param pageable Informações de paginação (use PageRequest.of(0, N))
     * @return Lista dos maiores créditos
     */
    @Query("SELECT c FROM Credito c ORDER BY c.valorIssqn DESC")
    List<Credito> findTopCreditosByValor(Pageable pageable);

    /**
     * Busca créditos mais recentes
     *
     * @param pageable Informações de paginação (use PageRequest.of(0, N))
     * @return Lista dos créditos mais recentes
     */
    @Query("SELECT c FROM Credito c ORDER BY c.dataConstituicao DESC")
    List<Credito> findCreditosMaisRecentes(Pageable pageable);

    // ================================================
    // CONSULTAS DE AUDITORIA (JPQL PURO)
    // ================================================

    /**
     * Busca estatísticas mensais usando JPQL (portável)
     *
     * @param ano Ano para consulta
     * @return Lista de arrays com [mes, quantidade, valorTotal, valorMedio]
     */
    @Query("SELECT " +
            "FUNCTION('EXTRACT', 'MONTH', c.dataConstituicao) as mes, " +
            "COUNT(c) as quantidade, " +
            "SUM(c.valorIssqn) as valorTotal, " +
            "AVG(c.valorIssqn) as valorMedio " +
            "FROM Credito c " +
            "WHERE FUNCTION('EXTRACT', 'YEAR', c.dataConstituicao) = :ano " +
            "GROUP BY FUNCTION('EXTRACT', 'MONTH', c.dataConstituicao) " +
            "ORDER BY FUNCTION('EXTRACT', 'MONTH', c.dataConstituicao)")
    List<Object[]> findEstatisticasMensais(@Param("ano") int ano);

    /**
     * Busca duplicatas de números de crédito (para auditoria)
     *
     * @return Lista de números de crédito duplicados com contagem
     */
    @Query("SELECT c.numeroCredito, COUNT(c) " +
            "FROM Credito c " +
            "GROUP BY c.numeroCredito " +
            "HAVING COUNT(c) > 1 " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> findNumeroCreditoDuplicados();

    /**
     * Busca duplicatas de NFS-e (para auditoria)
     *
     * @return Lista de números de NFS-e duplicados com contagem
     */
    @Query("SELECT c.numeroNfse, COUNT(c) " +
            "FROM Credito c " +
            "GROUP BY c.numeroNfse " +
            "HAVING COUNT(c) > 1 " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> findNfseDuplicadas();

    /**
     * Conta total de créditos por tipo (JPQL puro)
     *
     * @return Lista de arrays com [tipo, total]
     */
    @Query("SELECT c.tipoCredito, COUNT(c) " +
            "FROM Credito c " +
            "GROUP BY c.tipoCredito " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> countCreditosPorTipo();
}