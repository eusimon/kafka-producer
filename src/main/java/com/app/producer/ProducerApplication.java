package com.app.producer;

import com.app.producer.enums.AlarmType;
import com.app.producer.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class ProducerApplication {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Value(value = "${message.topic.name}")
    private String messageTopic;

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    private ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);

    }

    @Scheduled(fixedRate = 10000)
    public void sendMessage() {
        List<Message> messages = AlarmType.getDbTypes().stream()
                                          .map(name -> new Message(Instant.now().getEpochSecond(), name, getRandomNumberInRange(1, 10)))
                                          .collect(Collectors.toList());

        Flux<ProducerRecord<String, String>> outFlux = Flux.fromIterable(messages)
                                                           .map(msg -> new ProducerRecord<>(messageTopic, String.valueOf(msg.getTimestamp()), convertToString(msg)));
        kafkaSender.send(outFlux.map(rec -> SenderRecord.create(rec, rec.key())))
                   .subscribe(msg -> LOG.info("timestamp [{}], offset [{}]",
                                              msg.recordMetadata().timestamp(),
                                              msg.recordMetadata().offset()));
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().orElse(0);

    }

    private String convertToString(Message message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
