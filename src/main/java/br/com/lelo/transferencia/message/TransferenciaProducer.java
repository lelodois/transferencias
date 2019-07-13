package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.model.Tansferencia;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static br.com.lelo.transferencia.model.ContaTopic.COM_MOVIMENTAR;

public class TransferenciaProducer {

    public void transferir(int size) throws Exception {
        Random rdm = new Random();
        KafkaProducer<String, String> kafkaProducer = KafkaPropertiesBase.baseProducer();
        for (int index = 0; index <= size; index++) {
            BigDecimal valor = new BigDecimal(rdm.nextDouble()).setScale(2, RoundingMode.HALF_UP);
            Tansferencia tansferencia = new Tansferencia(rdm.nextInt(), rdm.nextInt(10), rdm.nextInt(10), valor);

            ProducerRecord<String, String> record = new ProducerRecord<>(COM_MOVIMENTAR.getTopic(), tansferencia.asJson());
            kafkaProducer.send(record);
        }
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
