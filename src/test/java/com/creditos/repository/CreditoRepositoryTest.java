package com.creditos.repository;

import com.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditoRepositoryTest {

    @Autowired
    private CreditoRepository creditoRepository;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        creditoRepository.deleteAll();

        // Insere dados de teste consistentes
        Credito credito1 = new Credito();
        credito1.setNumeroCredito("CRD001");
        credito1.setNumeroNfse("NFS123");
        credito1.setTipoCredito("ISSQN");
        credito1.setValorIssqn(new BigDecimal("150.00"));
        credito1.setDataConstituicao(LocalDate.now());
        credito1.setValorFaturado(new BigDecimal("1000.00"));
        credito1.setValorDeducao(new BigDecimal("100.00"));
        credito1.setBaseCalculo(new BigDecimal("900.00"));
        credito1.setAliquota(new BigDecimal("5.00"));
        credito1.setSimplesNacional(true);

        Credito credito2 = new Credito();
        credito2.setNumeroCredito("CRD002");
        credito2.setNumeroNfse("NFS456");
        credito2.setTipoCredito("ICMS");
        credito2.setValorIssqn(new BigDecimal("250.00"));
        credito2.setDataConstituicao(LocalDate.now());
        credito2.setValorFaturado(new BigDecimal("2000.00"));
        credito2.setValorDeducao(new BigDecimal("200.00"));
        credito2.setBaseCalculo(new BigDecimal("1800.00"));
        credito2.setAliquota(new BigDecimal("7.00"));
        credito2.setSimplesNacional(false);

        creditoRepository.saveAll(Arrays.asList(credito1, credito2));
    }

    @Test
    @DisplayName("Deve buscar crédito por número da NFS-e")
    void testFindByNumeroNfse() {
        List<Credito> resultados = creditoRepository.findByNumeroNfse("NFS123");
        assertThat(resultados)
                .isNotEmpty()
                .allMatch(c -> c.getNumeroNfse().equals("NFS123"));
    }

    @Test
    @DisplayName("Deve buscar crédito por número do crédito")
    void testFindByNumeroCredito() {
        List<Credito> resultados = creditoRepository.findByNumeroCredito("CRD001");
        assertThat(resultados)
                .isNotEmpty()
                .allMatch(c -> c.getNumeroCredito().equals("CRD001"));
    }

    @Test
    @DisplayName("Deve buscar crédito por tipo (ignore case)")
    void testFindByTipoCreditoIgnoreCase() {
        List<Credito> resultados = creditoRepository.findByTipoCreditoIgnoreCase("issqn");
        assertThat(resultados)
                .isNotEmpty()
                .allMatch(c -> c.getTipoCredito().equalsIgnoreCase("ISSQN"));
    }

    @Test
    @DisplayName("Deve buscar crédito por faixa de valor ISSQN")
    void testFindByValorIssqnBetween() {
        List<Credito> resultados = creditoRepository.findByValorIssqnBetween(
                new BigDecimal("100.00"), new BigDecimal("300.00"));
        assertThat(resultados)
                .isNotEmpty()
                .allMatch(c -> c.getValorIssqn().compareTo(new BigDecimal("100.00")) >= 0 &&
                        c.getValorIssqn().compareTo(new BigDecimal("300.00")) <= 0);
    }

    @Test
    @DisplayName("Deve buscar créditos com valores inconsistentes")
    void testFindCreditosComValoresInconsistentes() {
        // Cria um crédito que atenda a todas as validações básicas
        // mas tenha valores inconsistentes na lógica de negócio
        Credito inconsistente = new Credito();
        inconsistente.setNumeroCredito("INCONSISTENTE");
        inconsistente.setNumeroNfse("NFS999");
        inconsistente.setTipoCredito("ISSQN");
        inconsistente.setValorIssqn(new BigDecimal("100.00"));
        inconsistente.setDataConstituicao(LocalDate.now());

        // Define valores inconsistentes (faturado - dedução deveria ser igual a base)
        inconsistente.setValorFaturado(new BigDecimal("1000.00"));
        inconsistente.setValorDeducao(new BigDecimal("900.00"));
        inconsistente.setBaseCalculo(new BigDecimal("200.00")); // Inconsistente

        // Garante outros campos obrigatórios
        inconsistente.setAliquota(new BigDecimal("5.00"));
        inconsistente.setSimplesNacional(true);

        creditoRepository.saveAndFlush(inconsistente); // Força o flush para detectar inconsistências

        List<Credito> inconsistentes = creditoRepository.findCreditosComValoresInconsistentes();
        assertThat(inconsistentes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve contar créditos por tipo")
    void testCountByTipoCredito() {
        Long count = creditoRepository.countByTipoCredito("ISSQN");
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar estatísticas por tipo de crédito")
    void testFindEstatisticasPorTipo() {
        List<?> estatisticas = creditoRepository.findEstatisticasPorTipo();
        assertThat(estatisticas).hasSize(2); // ISS e ICMS
    }

    @Test
    @DisplayName("Deve retornar o primeiro crédito por número do crédito")
    void testFindFirstByNumeroCredito() {
        Optional<Credito> credito = creditoRepository.findFirstByNumeroCredito("CRD001");
        assertThat(credito)
                .isPresent()
                .get()
                .extracting(Credito::getNumeroCredito)
                .isEqualTo("CRD001");
    }
}