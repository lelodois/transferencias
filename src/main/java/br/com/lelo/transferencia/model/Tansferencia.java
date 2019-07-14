package br.com.lelo.transferencia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

public class Tansferencia {
    private int id;
    private int contaOrigemId;
    private int contaDestinoId;
    private BigDecimal valor;

    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    public Tansferencia() {
    }

    public Tansferencia(int id, int contaOrigemId, int contaDestinoId, BigDecimal valor) {
        this.id = id;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.valor = valor;
    }

    public Tansferencia(String json) throws IOException {
        Tansferencia tansferencia = mapper.readValue(json, Tansferencia.class);
        this.valor = tansferencia.valor;
        this.id = tansferencia.id;
        this.contaOrigemId = tansferencia.contaOrigemId;
        this.contaDestinoId = tansferencia.contaDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public int getContaOrigemId() {
        return contaOrigemId;
    }

    public int getContaDestinoId() {
        return contaDestinoId;
    }

    public int getId() {
        return id;
    }

    public String asJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public void transferir(Conta contaOrigem, Conta contaDestino) {
        if (contaDestino.getTransferencias().contains(this) || contaOrigem.getTransferencias().contains(this)) {
            throw new RuntimeException("Transferencia repetida: " + this.id);
        }
        contaOrigem.debitar(this);
        contaDestino.creditar(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tansferencia that = (Tansferencia) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "de: " + contaOrigemId + " para: " + contaDestinoId + " R$ " + valor.doubleValue();
    }
}