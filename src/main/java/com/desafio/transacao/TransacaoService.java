package com.desafio.transacao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.desafio.model.EstatisticaResponse;
import com.desafio.model.Transacao;
import com.desafio.model.TransacaoRequest;

public class TransacaoService {
    
    private final TransacaoRepository repository;

    @Autowired 
    public TransacaoService(TransacaoRepository repository) {
        this.repository = repository; 
    }

public boolean processarTransacao(TransacaoRequest request) {

    OffsetDateTime dataHora = request.getDataHora();
    BigDecimal valor = request.getValor();

    if ((valor.compareTo(BigDecimal.ZERO) < 0) || (dataHora.isAfter(OffsetDateTime.now()))) {
        return false;
    } else {
        Transacao Transacao = new Transacao(valor, dataHora);
        repository.salvar(Transacao);
        return true;
    }

}

public EstatisticaResponse calcularEstatisticas() {
    Collection<Transacao> todasAsTransacoes = repository.buscarTodas();
    OffsetDateTime lastMinute = OffsetDateTime.now().minusSeconds(60);

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
