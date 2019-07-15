package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.TransferenciaApplication;
import br.com.lelo.transferencia.model.Conta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class ContaClientConsumer {

    private Logger logger = LoggerFactory.getLogger(ContaClientConsumer.class.getName());

    @KafkaListener(topics = TransferenciaApplication.EVT_CONTA_MOV, groupId = "group-1")
    public void transferenciaRealizada(String message) {
        try {
            Conta conta = new Conta(message);
            logger.info("Conta Movimentada: " + conta);
        } catch (Exception exc) {
            logger.error("Error when: " + message, exc.getMessage());
        }
    }

    @KafkaListener(topics = TransferenciaApplication.EVT_CONTA_MOV_ERRO, groupId = "group-1")
    public void transferenciaNaoRealizada(String message) {
        try {
            Conta conta = new Conta(message);
            logger.error("Conta Não Movimentada: " + conta);
        } catch (Exception exc) {
            logger.error("Error when: " + message, exc.getMessage());
        }
    }
}