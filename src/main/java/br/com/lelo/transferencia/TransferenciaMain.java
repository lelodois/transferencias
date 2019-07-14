package br.com.lelo.transferencia;

import br.com.lelo.transferencia.message.ContaClientConsumer;
import br.com.lelo.transferencia.message.TransferenciaConsumer;
import br.com.lelo.transferencia.message.TransferenciaProducer;

public class TransferenciaMain {

    public static void main(String[] args) throws Exception {
        new ContaClientConsumer().start("client-conta");
        new TransferenciaConsumer().start("domain-conta-01");
        new TransferenciaProducer().transferir(10);
    }
}
