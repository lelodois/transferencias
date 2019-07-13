package br.com.lelo.transferencia;

import br.com.lelo.transferencia.message.ContaConsumer;
import br.com.lelo.transferencia.message.TransferenciaConsumer;
import br.com.lelo.transferencia.message.TransferenciaProducer;

public class TransferenciaMain {

    public static void main(String[] args) throws Exception {
        new ContaConsumer().start("client-conta");
        new TransferenciaConsumer().start("domain-conta");
        new TransferenciaProducer().transferir(100000);
    }
}
