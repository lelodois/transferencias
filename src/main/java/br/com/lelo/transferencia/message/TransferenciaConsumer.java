package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.dao.ContaRepository;
import br.com.lelo.transferencia.model.Conta;
import br.com.lelo.transferencia.model.ContaTopic;
import br.com.lelo.transferencia.model.Tansferencia;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static br.com.lelo.transferencia.message.KafkaPropertiesBase.baseConsumer;
import static br.com.lelo.transferencia.model.ContaTopic.*;
import static java.time.Duration.ofMillis;

public class TransferenciaConsumer {

    private ContaRepository repository = ContaRepository.get();
    private Logger logger = LoggerFactory.getLogger(TransferenciaConsumer.class.getName());
    private KafkaProducer<String, String> kafkaProducer = KafkaPropertiesBase.baseProducer();

    public void start(String groupId) {
        new Thread(() -> {
            KafkaConsumer<String, String> consumer = baseConsumer(groupId, COM_MOVIMENTAR);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        this.transferir(new Tansferencia(record.value()));
                    } catch (Exception exc) {
                        logger.error("Error when: " + record.value(), exc.getMessage());
                    }
                }
            }
        }).start();
    }

    private void transferir(Tansferencia tansferencia) throws JsonProcessingException {
        Conta contaOrigem = repository.getConta(tansferencia.getContaOrigemId());
        Conta contaDestino = repository.getConta(tansferencia.getContaDestinoId());
        try {
            tansferencia.transferir(contaOrigem, contaDestino);
            this.finalizarConta(contaDestino);
        } catch (Exception e) {
            logger.error(tansferencia + " error: " + e.getMessage());
            informarTransferencia(EVT_CONTA_MOV_ERRO, contaOrigem);
        }
    }

    public void informarTransferencia(ContaTopic contaTopic, Conta conta) throws JsonProcessingException {
        kafkaProducer.send(new ProducerRecord<>(contaTopic.getTopic(), conta.asJson()));
        kafkaProducer.flush();
    }

    private void finalizarConta(Conta... contas) throws JsonProcessingException {
        for (Conta conta : contas) {
            repository.save(conta);
            informarTransferencia(EVT_CONTA_MOV, conta);
        }
    }
}