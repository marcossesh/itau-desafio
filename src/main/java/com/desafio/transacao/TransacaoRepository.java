package com.desafio.transacao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.desafio.model.Transacao;

public class TransacaoRepository {
    private static final Map<Long, Transacao> transacaoMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TransacaoRepository.class);
    private static final AtomicLong nextId = new AtomicLong(1);


    public void salvar(Transacao transacao){
        Long idTransacao = nextId.getAndIncrement();
        transacaoMap.put(idTransacao, transacao);
        logger.info("Transação salva com o ID: {}", idTransacao);
        }

    public Collection<Transacao> buscarTodas() {
        return Collections.unmodifiableCollection(transacaoMap.values());
    }

    public void limpar() {
        transacaoMap.clear();
    }
    

}
