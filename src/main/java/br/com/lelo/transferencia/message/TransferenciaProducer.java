package br.com.lelo.transferencia.message;

import br.com.lelo.transferencia.model.Tansferencia;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static br.com.lelo.transferencia.TransferenciaApplication.COM_MOVIMENTAR;

@Component
public class TransferenciaProducer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostConstruct
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            Random rdm = new Random();
            for (int index = 0; index <= 100; index++) {
                BigDecimal valor = new BigDecimal(rdm.nextDouble()).setScale(2, RoundingMode.HALF_UP);
                Tansferencia tansferencia = new Tansferencia(rdm.nextInt(), rdm.nextInt(10), rdm.nextInt(10), valor);

                kafkaTemplate.send(new ProducerRecord<>(COM_MOVIMENTAR, tansferencia.asJson()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
