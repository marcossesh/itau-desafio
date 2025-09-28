package com.desafio.transacao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.desafio.model.EstatisticaResponse;
import com.desafio.model.Transacao;
import com.desafio.model.TransacaoRequest;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @Mock
    private TransacaoRepository repository;

    @InjectMocks
    private TransacaoService service;

    private static final long JANELA_SEGUNDOS_TESTE = 60;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "janelaSegundos", JANELA_SEGUNDOS_TESTE);
    }

    @Test
    void deveRetornarTrueESalvar_QuandoTransacaoForValida() {
        // ARRANGE
        TransacaoRequest request = new TransacaoRequest(
            new BigDecimal("10.50"), 
            OffsetDateTime.now().minusSeconds(1)
        );

        // ACT
        boolean resultado = service.processarTransacao(request);

        // ASSERT
        assertTrue(resultado); 
        verify(repository, times(1)).salvar(any(Transacao.class));
    }

    @Test
    void deveRetornarFalse_QuandoTransacaoTiverValorNegativo() {
        // ARRANGE
        TransacaoRequest request = new TransacaoRequest(
            new BigDecimal("-0.01"), 
            OffsetDateTime.now()
        );

        // ACT
        boolean resultado = service.processarTransacao(request);

        // ASSERT
        assertFalse(resultado); 
        verify(repository, never()).salvar(any(Transacao.class)); 
    }

    @Test
    void deveRetornarFalse_QuandoTransacaoTiverDataNoFuturo() {
        // ARRANGE
        TransacaoRequest request = new TransacaoRequest(
            new BigDecimal("10.50"), 
            OffsetDateTime.now().plusSeconds(1)
        );

        // ACT
        boolean resultado = service.processarTransacao(request);

        // ASSERT
        assertFalse(resultado);
        verify(repository, never()).salvar(any(Transacao.class));
    }

    @Test
    void deveRetornarEstatisticasZeradas_QuandoNaoHouverTransacoes() {
        // ARRANGE
        when(repository.buscarTodas()).thenReturn(Collections.emptyList());

        // ACT
        EstatisticaResponse response = service.calcularEstatisticas();

        // ASSERT
        assertEquals(0L, response.getCount());
        assertEquals(0.0, response.getSum(), 0.001);
        assertEquals(0.0, response.getAvg(), 0.001);
        assertEquals(0.0, response.getMin(), 0.001);
        assertEquals(0.0, response.getMax(), 0.001);
    }

    @Test
    void deveCalcularEstatisticasCorretamente_ParaTransacoesValidas() {
        // ARRANGE
        OffsetDateTime agora = OffsetDateTime.now().minusSeconds(1);
        
        Collection<Transacao> transacoes = Arrays.asList(
            new Transacao(new BigDecimal("10.00"), agora),
            new Transacao(new BigDecimal("20.00"), agora.minusSeconds(10)),
            new Transacao(new BigDecimal("30.00"), agora.minusSeconds(20))
        );
        when(repository.buscarTodas()).thenReturn(transacoes);

        // ACT
        EstatisticaResponse response = service.calcularEstatisticas();

        // ASSERT
        assertEquals(3L, response.getCount());
        assertEquals(60.00, response.getSum(), 0.001); // 10 + 20 + 30
        assertEquals(20.00, response.getAvg(), 0.001); // 60 / 3
        assertEquals(10.00, response.getMin(), 0.001);
        assertEquals(30.00, response.getMax(), 0.001);
    }

    @Test
    void deveFiltrarCorretamente_TransacoesMaisAntigasQueJanela() {
        // ARRANGE
        OffsetDateTime agora = OffsetDateTime.now();

        Collection<Transacao> transacoes = Arrays.asList(
            // Transação VÁLIDA (1 segundo atrás)
            new Transacao(new BigDecimal("50.00"), agora.minusSeconds(1)), 
            // Transação INVÁLIDA (70 segundos atrás)
            new Transacao(new BigDecimal("10.00"), agora.minusSeconds(70)), 
            // Transação INVÁLIDA (200 segundos atrás)
            new Transacao(new BigDecimal("30.00"), agora.minusSeconds(200)), 
            // Transação VÁLIDA (59 segundos atrás)
            new Transacao(new BigDecimal("20.00"), agora.minusSeconds(59))
        );
        when(repository.buscarTodas()).thenReturn(transacoes);

        // ACT
        EstatisticaResponse response = service.calcularEstatisticas();

        // ASSERT
        // Apenas duas transações (50.00 e 20.00) devem ser consideradas
        assertEquals(2L, response.getCount()); 
        assertEquals(70.00, response.getSum(), 0.001); // 50 + 20
        assertEquals(35.00, response.getAvg(), 0.001); // 70 / 2
        assertEquals(20.00, response.getMin(), 0.001);
        assertEquals(50.00, response.getMax(), 0.001);
    }

    @Test
    void deveChamarLimparUmaVez_AoDeletarTodasTransacoes() {
        // ACT
        service.deletarTodasTransacao();

        // ASSERT
        verify(repository, times(1)).limpar(); 
    }
}