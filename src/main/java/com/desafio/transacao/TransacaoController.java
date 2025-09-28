package com.desafio.transacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desafio.model.EstatisticaResponse;
import com.desafio.model.TransacaoRequest;

@RestController
public class TransacaoController {
    private static final Logger logger = LoggerFactory.getLogger(TransacaoController.class);

    private final TransacaoService service;

    @Autowired
    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping("/transacao")
    public ResponseEntity<Void> criarTransacao(@RequestBody TransacaoRequest request) {
        logger.info("POST /transacao chamado. Dados: {}", request.toString());
        boolean sucesso = service.processarTransacao(request);
        
        if (sucesso) {
            logger.info("Transação aceita. Status 201.");
            return ResponseEntity.status(201).build();
        } else {
            logger.warn("Transação rejeitada pelo service. Status 422.");
            return ResponseEntity.status(422).build();
        }
    }

    @DeleteMapping("/transacao")
    public ResponseEntity<Void> limparTransacoes() {
        service.deletarTodasTransacao();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/estatistica")
    public ResponseEntity<EstatisticaResponse> calcularEstatisticas() {
        EstatisticaResponse estatisticas = service.calcularEstatisticas();

        return ResponseEntity.ok(estatisticas);
    }
}