package br.com.lelo.transferencia.dao;

import br.com.lelo.transferencia.model.Conta;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class ContaRepository {

    private final static ContaRepository contaRepository = new ContaRepository();
    private static final String PREFIX = "key-conta-";
    private final Jedis jedis = new Jedis("localhost", 6379);

    private ContaRepository() {
    }

    public static ContaRepository get() {
        return contaRepository;
    }

    public Set<String> findAll() {
        return jedis.keys(PREFIX + "*");
    }

    public Conta getConta(int contaId) {
        try {
            String contaJson = jedis.get(PREFIX + contaId);
            if (contaJson != null) {
                return new Conta(contaJson);
            }
            return save(Conta.novaConta(contaId));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar a conta: " + contaId, e);
        }
    }

    public Conta save(Conta conta) {
        try {
            jedis.set(PREFIX + conta.getContaId(), conta.asJson());
            return conta;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao salvar a conta: " + conta.getContaId(), e);
        }
    }
}
