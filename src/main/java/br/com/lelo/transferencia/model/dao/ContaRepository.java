package br.com.lelo.transferencia.model.dao;

import br.com.lelo.transferencia.model.Conta;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.Jedis;

import static br.com.lelo.transferencia.model.Conta.novaConta;
import static java.time.LocalDateTime.now;

public class ContaRepository {

    private final static ContaRepository contaRepository = new ContaRepository();
    private static final String PREFIX = "key-conta-";
    private final Jedis jedis = new Jedis("localhost", 6379);

    private ContaRepository() {
    }

    public static ContaRepository instance() {
        return contaRepository;
    }

    public Conta getConta(int contaId) {
        try {
            String contaJson = jedis.get(PREFIX + contaId);
            if (contaJson != null) {
                return new Conta(contaJson);
            }
            return saveConta(novaConta(contaId));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar a conta: " + contaId, e);
        }
    }

    public Conta saveConta(Conta conta) {
        try {
            jedis.set(PREFIX + conta.getContaId(), conta.asJson());
            return conta;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao salvar a conta: " + conta.getContaId(), e);
        }
    }

    public void saveTransferencia(int id) {
        jedis.set(PREFIX + "-TRANS-" + id, now().toString());
    }

    public boolean containsTransferencia(int id) {
        return jedis.get(PREFIX + "-TRANS-" + id) != null;
    }
}
