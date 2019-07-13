package br.com.lelo.transferencia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

public class Conta {
    private Integer contaId;
    private BigDecimal saldo;
    private List<Tansferencia> transferencias = new ArrayList<>();

    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    public Conta() {
    }

    public Conta(String contaJson) throws IOException {
        Conta conta = mapper.readValue(contaJson, Conta.class);
        this.contaId = conta.contaId;
        this.transferencias = conta.transferencias;
        this.saldo = conta.saldo;
    }

    public static Conta novaConta(int contaId) {
        Conta conta = new Conta();
        conta.contaId = contaId;
        conta.saldo = new BigDecimal("1.00");
        conta.transferencias = new ArrayList<>();
        return conta;
    }

    public void debitar(Tansferencia tansferencia) {
        if (saldo.compareTo(tansferencia.getValor()) <= 0) {
            throw new RuntimeException("Conta sem saldo para debitar");
        }
        saldo = saldo.subtract(tansferencia.getValor()).setScale(2, HALF_UP);
        this.transferencias.add(tansferencia);
    }

    public void creditar(Tansferencia tansferencia) {
        saldo = saldo.add(tansferencia.getValor()).setScale(2, HALF_UP);
        this.transferencias.add(tansferencia);
    }

    public int getContaId() {
        return contaId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public List<Tansferencia> getTransferencias() {
        return transferencias;
    }

    public String asJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "id: " + contaId + " saldo: " + saldo + " transferencias: " + transferencias;
    }
}