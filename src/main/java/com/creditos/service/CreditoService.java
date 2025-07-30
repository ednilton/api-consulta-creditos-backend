package com.creditos.service;

import com.creditos.dto.CreditoResponseDTO;
import com.creditos.entity.Credito;
import com.creditos.exception.CreditoException;
import com.creditos.repository.CreditoRepository;
import com.creditos.util.LoggingUtils;
import com.creditos.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service completo para lógica de negócio dos créditos constituídos
 * Implementa todas as operações relacionadas a créditos de ISSQN
 * Refatorado seguindo princípios SOLID, DRY e KISS
 * Atualizado com novo sistema de tratamento de erros padronizado
 *
 * @author Ednilton Curt Rauh
 * @version 2.1.0
 */
@Service
@Transactional(readOnly = true)
public class CreditoService {

    private static final Logger logger = LoggerFactory.getLogger(CreditoService.class);

    private final CreditoRepository creditoRepository;

    @Autowired
    public CreditoService(CreditoRepository creditoRepository) {
        this.creditoRepository = creditoRepository;
    }

    // ================================================
    // MÉTODOS PRINCIPAIS DE CONSULTA
    // ================================================

    /**
     * Consulta créditos pelo número da NFS-e
     * Método principal conforme especificação do desafio técnico
     *
     * @param numeroNfse Número da NFS-e para consulta
     * @return Lista de créditos encontrados
     * @throws CreditoException se nenhum crédito for encontrado ou erro de validação
     */
    @Cacheable(value = "creditos", key = "'nfse_' + #numeroNfse")
    public List<CreditoResponseDTO> consultarCreditosPorNfse(String numeroNfse) {
        LoggingUtils.logInicioConsulta(logger, "NFS-e", numeroNfse);

        try {
            // Validação usando nosso sistema de exceções
            validarNumeroNfse(numeroNfse);

            List<Credito> creditos = buscarCreditosPorNfse(numeroNfse);
            validateCreditosEncontrados(creditos, numeroNfse);

            List<CreditoResponseDTO> response = convertToDTOList(creditos);
            LoggingUtils.logConsultaSucesso(logger, "NFS-e", numeroNfse, response.size());

            return response;

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na consulta por NFS-e {}: {}", numeroNfse, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta de créditos por NFS-e: " + ex.getMessage());
        }
    }

    /**
     * Consulta um crédito específico pelo número do crédito
     * Método principal conforme especificação do desafio técnico
     *
     * @param numeroCredito Número do crédito constituído
     * @return Dados do crédito encontrado
     * @throws CreditoException se o crédito não for encontrado ou erro de validação
     */
    @Cacheable(value = "creditos", key = "'credito_' + #numeroCredito")
    public CreditoResponseDTO consultarCreditoPorNumero(String numeroCredito) {
        LoggingUtils.logInicioConsulta(logger, "número do crédito", numeroCredito);

        try {
            // Validação usando nosso sistema de exceções
            validarNumeroCredito(numeroCredito);

            Credito credito = buscarCreditoPorNumero(numeroCredito);
            CreditoResponseDTO response = convertToDTO(credito);

            LoggingUtils.logConsultaSucesso(logger, "número do crédito", numeroCredito, 1);

            return response;

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na consulta por número do crédito {}: {}", numeroCredito, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta do crédito: " + ex.getMessage());
        }
    }

    // ================================================
    // MÉTODOS DE VERIFICAÇÃO DE EXISTÊNCIA
    // ================================================

    /**
     * Verifica se um crédito existe pelo número
     *
     * @param numeroCredito Número do crédito
     * @return true se existir, false caso contrário
     * @throws CreditoException se erro de validação ou interno
     */
    @Cacheable(value = "existencia", key = "'credito_exists_' + #numeroCredito")
    public boolean existeCreditoPorNumero(String numeroCredito) {
        try {
            // Usa validação não-destrutiva do ValidationUtils
            if (!ValidationUtils.isValidNumeroCredito(numeroCredito)) {
                throw CreditoException.numeroCreditoInvalido(numeroCredito, "formato inválido");
            }

            String numeroNormalizado = ValidationUtils.normalizeString(numeroCredito);
            List<Credito> creditos = creditoRepository.findByNumeroCredito(numeroNormalizado);
            boolean existe = !creditos.isEmpty();

            logger.debug("Verificação de existência para crédito {}: {}", numeroCredito, existe);
            return existe;

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas (erros de validação)
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na verificação de existência do crédito {}: {}", numeroCredito, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação de existência do crédito: " + ex.getMessage());
        }
    }

    /**
     * Verifica se existe crédito para uma NFS-e
     *
     * @param numeroNfse Número da NFS-e
     * @return true se existir, false caso contrário
     * @throws CreditoException se erro de validação ou interno
     */
    @Cacheable(value = "existencia", key = "'nfse_exists_' + #numeroNfse")
    public boolean existeCreditoPorNfse(String numeroNfse) {
        try {
            // Usa validação não-destrutiva do ValidationUtils
            if (!ValidationUtils.isValidNumeroNfse(numeroNfse)) {
                throw CreditoException.numeroNfseInvalido(numeroNfse, "formato inválido");
            }

            String numeroNormalizado = ValidationUtils.normalizeString(numeroNfse);
            boolean existe = !creditoRepository.findByNumeroNfse(numeroNormalizado).isEmpty();

            logger.debug("Verificação de existência para NFS-e {}: {}", numeroNfse, existe);
            return existe;

        } catch (CreditoException ex) {
            // Re-lança exceções já tratadas (erros de validação)
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na verificação de existência da NFS-e {}: {}", numeroNfse, ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na verificação de existência da NFS-e: " + ex.getMessage());
        }
    }

    // ================================================
    // MÉTODOS ADMINISTRATIVOS
    // ================================================

    /**
     * Lista todos os créditos (método auxiliar para administração)
     *
     * @return Lista de todos os créditos
     * @throws CreditoException se erro interno
     */
    public List<CreditoResponseDTO> listarTodosCreditos() {
        logger.debug("Listando todos os créditos");

        try {
            List<Credito> creditos = creditoRepository.findAll();
            List<CreditoResponseDTO> response = convertToDTOList(creditos);

            logger.info("Listagem completa realizada. {} crédito(s) encontrado(s)", response.size());
            return response;

        } catch (Exception ex) {
            logger.error("Erro interno na listagem de todos os créditos: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na listagem de créditos: " + ex.getMessage());
        }
    }

    /**
     * Lista créditos com paginação
     *
     * @param pageable Informações de paginação
     * @return Página de créditos
     * @throws CreditoException se erro interno ou parâmetros inválidos
     */
    public Page<CreditoResponseDTO> listarCreditosPaginados(Pageable pageable) {
        logger.debug("Listando créditos paginados - página: {}, tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            if (pageable == null) {
                throw CreditoException.erroInterno("Parâmetros de paginação são obrigatórios");
            }

            Page<Credito> creditosPage = creditoRepository.findAll(pageable);
            return creditosPage.map(this::convertToDTO);

        } catch (CreditoException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na listagem paginada: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na listagem paginada de créditos: " + ex.getMessage());
        }
    }

    /**
     * Conta o total de créditos cadastrados
     *
     * @return Número total de créditos
     * @throws CreditoException se erro interno
     */
    public long contarTotalCreditos() {
        try {
            long total = creditoRepository.count();
            logger.debug("Total de créditos no sistema: {}", total);
            return total;

        } catch (Exception ex) {
            logger.error("Erro interno na contagem de créditos: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na contagem de créditos: " + ex.getMessage());
        }
    }

    // ================================================
    // MÉTODOS DE CONSULTA AVANÇADA
    // ================================================

    /**
     * Consulta créditos por período de constituição
     *
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de créditos no período
     * @throws CreditoException se parâmetros inválidos ou erro interno
     */
    public List<CreditoResponseDTO> consultarCreditosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LoggingUtils.logInicioConsulta(logger, "período", dataInicio + " a " + dataFim);

        try {
            // Validações específicas
            if (dataInicio == null || dataFim == null) {
                throw CreditoException.erroInterno("Datas de início e fim são obrigatórias");
            }

            if (dataInicio.isAfter(dataFim)) {
                throw CreditoException.erroInterno("Data de início deve ser anterior à data fim");
            }

            List<Credito> creditos = creditoRepository.findByDataConstituicaoBetween(dataInicio, dataFim);
            List<CreditoResponseDTO> response = convertToDTOList(creditos);

            LoggingUtils.logConsultaSucesso(logger, "período", dataInicio + " a " + dataFim, response.size());
            return response;

        } catch (CreditoException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na consulta por período: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta por período: " + ex.getMessage());
        }
    }

    /**
     * Consulta créditos por tipo
     *
     * @param tipoCredito Tipo do crédito
     * @return Lista de créditos do tipo especificado
     * @throws CreditoException se parâmetros inválidos ou erro interno
     */
    public List<CreditoResponseDTO> consultarCreditosPorTipo(String tipoCredito) {
        LoggingUtils.logInicioConsulta(logger, "tipo", tipoCredito);

        try {
            if (!ValidationUtils.isValidString(tipoCredito)) {
                throw CreditoException.erroInterno("Tipo do crédito é obrigatório");
            }

            List<Credito> creditos = creditoRepository.findByTipoCreditoIgnoreCase(tipoCredito.trim());
            List<CreditoResponseDTO> response = convertToDTOList(creditos);

            LoggingUtils.logConsultaSucesso(logger, "tipo", tipoCredito, response.size());
            return response;

        } catch (CreditoException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na consulta por tipo: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta por tipo: " + ex.getMessage());
        }
    }

    /**
     * Consulta créditos do Simples Nacional
     *
     * @param simplesNacional true para Simples Nacional, false para não optantes
     * @return Lista de créditos filtrados
     * @throws CreditoException se erro interno
     */
    public List<CreditoResponseDTO> consultarCreditosPorSimplesNacional(boolean simplesNacional) {
        String filtro = simplesNacional ? "Simples Nacional" : "Não optantes";
        LoggingUtils.logInicioConsulta(logger, "Simples Nacional", filtro);

        try {
            List<Credito> creditos = creditoRepository.findBySimplesNacional(simplesNacional);
            List<CreditoResponseDTO> response = convertToDTOList(creditos);

            LoggingUtils.logConsultaSucesso(logger, "Simples Nacional", filtro, response.size());
            return response;

        } catch (Exception ex) {
            logger.error("Erro interno na consulta por Simples Nacional: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta por Simples Nacional: " + ex.getMessage());
        }
    }

    /**
     * Consulta créditos por faixa de valor
     *
     * @param valorMinimo Valor mínimo do ISSQN
     * @param valorMaximo Valor máximo do ISSQN
     * @return Lista de créditos na faixa de valor
     * @throws CreditoException se parâmetros inválidos ou erro interno
     */
    public List<CreditoResponseDTO> consultarCreditosPorFaixaValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        LoggingUtils.logInicioConsulta(logger, "faixa de valor", valorMinimo + " a " + valorMaximo);

        try {
            // Validações específicas
            if (valorMinimo == null || valorMaximo == null) {
                throw CreditoException.erroInterno("Valores mínimo e máximo são obrigatórios");
            }

            if (valorMinimo.compareTo(valorMaximo) > 0) {
                throw CreditoException.erroInterno("Valor mínimo deve ser menor ou igual ao valor máximo");
            }

            List<Credito> creditos = creditoRepository.findByValorIssqnBetween(valorMinimo, valorMaximo);
            List<CreditoResponseDTO> response = convertToDTOList(creditos);

            LoggingUtils.logConsultaSucesso(logger, "faixa de valor", valorMinimo + " a " + valorMaximo, response.size());
            return response;

        } catch (CreditoException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro interno na consulta por faixa de valor: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha na consulta por faixa de valor: " + ex.getMessage());
        }
    }

    // ================================================
    // MÉTODOS DE ESTATÍSTICAS
    // ================================================

    /**
     * Calcula o valor total de ISSQN constituído
     *
     * @return Valor total em BigDecimal
     * @throws CreditoException se erro interno
     */
    public BigDecimal calcularValorTotalIssqn() {
        try {
            BigDecimal total = creditoRepository.sumValorIssqn();
            logger.info("Valor total de ISSQN constituído: {}", total);
            return total != null ? total : BigDecimal.ZERO;

        } catch (Exception ex) {
            logger.error("Erro interno no cálculo do valor total: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha no cálculo do valor total de ISSQN: " + ex.getMessage());
        }
    }

    /**
     * Calcula estatísticas por tipo de crédito
     *
     * @return Lista de estatísticas por tipo
     * @throws CreditoException se erro interno
     */
    public List<EstatisticasPorTipo> calcularEstatisticasPorTipo() {
        logger.debug("Calculando estatísticas por tipo de crédito");

        try {
            return creditoRepository.findEstatisticasPorTipo();

        } catch (Exception ex) {
            logger.error("Erro interno no cálculo de estatísticas por tipo: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha no cálculo de estatísticas por tipo: " + ex.getMessage());
        }
    }

    /**
     * Calcula a média de valores de ISSQN
     *
     * @return Valor médio
     * @throws CreditoException se erro interno
     */
    public BigDecimal calcularMediaValorIssqn() {
        try {
            BigDecimal media = creditoRepository.avgValorIssqn();
            logger.debug("Média de valor ISSQN: {}", media);
            return media != null ? media : BigDecimal.ZERO;

        } catch (Exception ex) {
            logger.error("Erro interno no cálculo da média: {}", ex.getMessage(), ex);
            throw CreditoException.erroInterno("Falha no cálculo da média de valores: " + ex.getMessage());
        }
    }

    // ================================================
    // MÉTODOS DE VALIDAÇÃO ESPECÍFICOS
    // ================================================

    /**
     * Validação específica para número de crédito usando ValidationUtils
     */
    private void validarNumeroCredito(String numeroCredito) {
        ValidationUtils.validateNumeroCredito(numeroCredito);
    }

    /**
     * Validação específica para número de NFS-e usando ValidationUtils
     */
    private void validarNumeroNfse(String numeroNfse) {
        ValidationUtils.validateNumeroNfse(numeroNfse);
    }

    // ================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ================================================

    /**
     * Busca créditos por NFS-e no repositório
     */
    private List<Credito> buscarCreditosPorNfse(String numeroNfse) {
        String numeroNormalizado = ValidationUtils.normalizeString(numeroNfse);
        return creditoRepository.findByNumeroNfse(numeroNormalizado);
    }

    /**
     * Busca um crédito específico por número
     * Atualizado para usar o repository refatorado
     */
    private Credito buscarCreditoPorNumero(String numeroCredito) {
        String numeroNormalizado = ValidationUtils.normalizeString(numeroCredito);
        List<Credito> creditos = creditoRepository.findByNumeroCredito(numeroNormalizado);

        if (creditos.isEmpty()) {
            LoggingUtils.logNenhumResultado(logger, "número do crédito", numeroCredito);
            throw CreditoException.creditoNaoEncontrado(numeroCredito);
        }

        // Se houver múltiplos, retorna o primeiro (mais antigo por ID)
        if (creditos.size() > 1) {
            logger.warn("Múltiplos créditos encontrados para número {}: {} registros. Retornando o primeiro.",
                    numeroCredito, creditos.size());
        }

        return creditos.get(0);
    }

    /**
     * Valida se foram encontrados créditos para a NFS-e
     */
    private void validateCreditosEncontrados(List<Credito> creditos, String numeroNfse) {
        if (creditos.isEmpty()) {
            LoggingUtils.logNenhumResultado(logger, "NFS-e", numeroNfse);
            throw CreditoException.nfseSemCreditos(numeroNfse);
        }
    }

    /**
     * Converte lista de entidades para lista de DTOs
     */
    private List<CreditoResponseDTO> convertToDTOList(List<Credito> creditos) {
        return creditos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade Credito para DTO de resposta
     * Seguindo exatamente o formato JSON especificado
     *
     * @param credito Entidade a ser convertida
     * @return DTO de resposta
     */
    private CreditoResponseDTO convertToDTO(Credito credito) {
        if (credito == null) {
            return null;
        }

        return new CreditoResponseDTO(
                credito.getNumeroCredito(),
                credito.getNumeroNfse(),
                credito.getDataConstituicao(),
                credito.getValorIssqn(),
                credito.getTipoCredito(),
                credito.getSimplesNacional(),
                credito.getAliquota(),
                credito.getValorFaturado(),
                credito.getValorDeducao(),
                credito.getBaseCalculo()
        );
    }

    // ================================================
    // CLASSES INTERNAS PARA ESTATÍSTICAS
    // ================================================

    /**
     * Classe para representar estatísticas por tipo de crédito
     */
    public static class EstatisticasPorTipo {
        private String tipoCredito;
        private Long quantidade;
        private BigDecimal valorTotal;
        private BigDecimal valorMedio;


        public EstatisticasPorTipo(String tipoCredito, Long quantidade, BigDecimal valorTotal, Double valorMedio) {
            this.tipoCredito = tipoCredito;
            this.quantidade = quantidade;
            this.valorTotal = valorTotal;
            this.valorMedio = valorMedio != null ? BigDecimal.valueOf(valorMedio) : BigDecimal.ZERO;
        }

        // Getters e Setters
        public String getTipoCredito() { return tipoCredito; }
        public void setTipoCredito(String tipoCredito) { this.tipoCredito = tipoCredito; }

        public Long getQuantidade() { return quantidade; }
        public void setQuantidade(Long quantidade) { this.quantidade = quantidade; }

        public BigDecimal getValorTotal() { return valorTotal; }
        public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

        public BigDecimal getValorMedio() { return valorMedio; }
        public void setValorMedio(BigDecimal valorMedio) { this.valorMedio = valorMedio; }
    }
}