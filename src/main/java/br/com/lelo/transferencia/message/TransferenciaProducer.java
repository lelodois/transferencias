package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.model.Tansferencia;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static br.com.lelo.transferencia.TransferenciaApplication.COM_MOVIMENTAR;
import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

@Component
public class TransferenciaProducer implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(TransferenciaProducer.class.getName());

    @Autowired
    private KafkaProducerProperties kafkaProducer;

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Random rdm = new Random();
        new Thread(() -> {
            while (true) {
                try {
                    KafkaProducer<String, String> producer = kafkaProducer.idempotenceProducer();
                    for (int index = 0; index <= 100; index++) {
                        BigDecimal valor = new BigDecimal(rdm.nextDouble()).setScale(2, RoundingMode.HALF_UP);
                        Tansferencia item = new Tansferencia(rdm.nextInt(), rdm.nextInt(10), rdm.nextInt(10), valor);
                        producer.send(new ProducerRecord<>(COM_MOVIMENTAR, "key-" + item.getId(), item.asJson()));
                    }
                    producer.flush();
                    producer.close();
                    sleep(ofSeconds(10).toMillis());
                } catch (Exception e) {
                    logger.error("Producer initial", e);
                }
            }
        }).start();
    }
}
