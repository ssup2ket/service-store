package com.ssup2ket.store.server.kafka.consumer;

import com.ssup2ket.store.server.kafka.model.InBox;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UserConsumer {
  @KafkaListener(topics = "outbox.event.User", groupId = "user")
  public void consume(InBox inbox) {
    log.info("-- consume -- " + inbox.getPayload());
  }
}
