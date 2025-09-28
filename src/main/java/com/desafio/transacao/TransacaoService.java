package com.desafio.transacao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.desafio.model.EstatisticaResponse;
import com.desafio.model.Transacao;
import com.desafio.model.TransacaoRequest;

@Service
public class TransacaoService {
    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);

    @Value("${transacao.janela-segundos:60}") 
    private long janelaSegundos;

    private final TransacaoRepository repository;

    @Autowired 
    public TransacaoService(TransacaoRepository repository) {
        this.repository = repository; 
    }

public boolean processarTransacao(TransacaoRequest request) {

    OffsetDateTime dataHora = request.getDataHora();
    BigDecimal valor = request.getValor();

    if ((valor.compareTo(BigDecimal.ZERO) < 0) || (dataHora.isAfter(OffsetDateTime.now()))) {
        logger.warn("Transação rejeitada: Valor negativo ou data futura. Valor: {}, Data: {}", valor, dataHora);
        return false;

    } else {
        Transacao transacao = new Transacao(valor, dataHora);
        repository.salvar(transacao);
        logger.info("Transação processada e salva com sucesso.");
        return true;
    }

}

public EstatisticaResponse calcularEstatisticas() {
    Collection<Transacao> todasAsTransacoes = repository.buscarTodas();
    OffsetDateTime lastMinute = OffsetDateTime.now().minusSeconds(60);
    logger.info("Calculando estatísticas. Janela de tempo: {} segundos. Total de transações: {}", this.janelaSegundos, todasAsTransacoes.size());

    java.util.DoubleSummaryStatistics stats = todasAsTransacoes.stream()
        .filter(transacao -> transacao.getDataHora().isAfter(lastMinute) || transacao.getDataHora().isEqual(lastMinute))
        .collect(Collectors.summarizingDouble(transacao -> transacao.getValor().doubleValue()));
        
    if (stats.getCount() == 0) {
        return new EstatisticaResponse(0, 0.0, 0.0, 0.0, 0.0);
    }
    return new EstatisticaResponse(
        stats.getCount(),
        stats.getSum(),
        stats.getAverage(),
        stats.getMin(),
        stats.getMax()
    );

}

public void deletarTodasTransacao() {
    repository.limpar();
}

}
