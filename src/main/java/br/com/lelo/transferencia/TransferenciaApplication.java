package br.com.lelo.transferencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransferenciaApplication {

    public static final String KAFKA_SERVER = "127.0.0.1:9092";
    public static final String COM_MOVIMENTAR = "com-movimentar";
    public static final String EVT_CONTA_MOV = "evt-conta-mov";
    public static final String EVT_CONTA_MOV_ERRO = "evt-conta-mov-erro";

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TransferenciaApplication.class, args);
    }
}
