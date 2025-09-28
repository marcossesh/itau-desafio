package com.desafio.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime; 

public class TransacaoRequest {

    private BigDecimal valor; 

    private OffsetDateTime dataHora; 

    public TransacaoRequest() {} 
    
    public TransacaoRequest(BigDecimal valor, OffsetDateTime dataHora) {
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(OffsetDateTime dataHora) {
        this.dataHora = dataHora;
    }
}