package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.TransferenciaApplication;
import br.com.lelo.transferencia.model.Conta;
import br.com.lelo.transferencia.model.Tansferencia;
import br.com.lelo.transferencia.model.dao.ContaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static br.com.lelo.transferencia.TransferenciaApplication.EVT_CONTA_MOV;
import static br.com.lelo.transferencia.TransferenciaApplication.EVT_CONTA_MOV_ERRO;

@Configuration
public class TransferenciaConsumer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ContaRepository repository = ContaRepository.instance();
    private Logger logger = LoggerFactory.getLogger(TransferenciaConsumer.class.getName());

    @KafkaListener(topics = TransferenciaApplication.COM_MOVIMENTAR, groupId = "group-1")
    public void transferir(String record) {
        try {
            Tansferencia transferencia = new Tansferencia(record);
            if (!repository.containsTransferencia(transferencia.getId())) {
                this.transferir(transferencia);
            }
        } catch (Exception exc) {
            logger.error(" - error: " + record, exc);
        }
    }

    private void transferir(Tansferencia item) throws JsonProcessingException {
        Conta contaOrigem = repository.getConta(item.getContaOrigemId());
        Conta contaDestino = repository.getConta(item.getContaDestinoId());
        try {
            item.transferir(contaOrigem, contaDestino);
            this.finalizarConta(contaDestino);
        } catch (Exception e) {
            logger.error(item + " error: " + e.getMessage());
            informarTransferencia(EVT_CONTA_MOV_ERRO, contaOrigem);
        } finally {
            repository.saveTransferencia(item.getId());
        }
    }

    public void informarTransferencia(String contaTopic, Conta conta) throws JsonProcessingException {
        kafkaTemplate.send(new ProducerRecord<>(contaTopic, conta.asJson())).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            public void onFailure(Throwable throwable) {
                logger.error("On send message transferencia", throwable);
            }

            public void onSuccess(SendResult<String, String> stringStringSendResult) {
            }
        });
    }

    private void finalizarConta(Conta... contas) throws JsonProcessingException {
        for (Conta conta : contas) {
            repository.saveConta(conta);
            informarTransferencia(EVT_CONTA_MOV, conta);
        }
    }
}