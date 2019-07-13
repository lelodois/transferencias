package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.model.Conta;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static br.com.lelo.transferencia.message.KafkaPropertiesBase.baseConsumer;
import static br.com.lelo.transferencia.model.ContaTopic.EVT_CONTA_MOV;
import static br.com.lelo.transferencia.model.ContaTopic.EVT_CONTA_MOV_ERRO;
import static java.time.Duration.ofMillis;

public class ContaConsumer {

    private Logger logger = LoggerFactory.getLogger(ContaConsumer.class.getName());

    public void start(String id) {

        new Thread(() -> {
            KafkaConsumer<String, String> consumer = baseConsumer(id, EVT_CONTA_MOV_ERRO, EVT_CONTA_MOV);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Conta conta = new Conta(record.value());
                        if (record.topic().equals(EVT_CONTA_MOV.getTopic())) {
                            logger.info("Conta Movimentada: " + conta);
                        } else {
                            logger.error("Conta Não Movimentada: " + conta);
                        }
                    } catch (Exception exc) {
                        logger.error("Error when: " + record.value(), exc.getMessage());
                    }
                }
            }
        }).start();
    }
}