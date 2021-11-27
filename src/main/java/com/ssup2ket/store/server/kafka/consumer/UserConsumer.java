package com.ssup2ket.store.server.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.domain.model.UserInfoRemoved;
import com.ssup2ket.store.domain.service.ManagementService;
import com.ssup2ket.store.server.kafka.model.InBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {
  @Autowired private ManagementService managementService;

  @KafkaListener(topics = "outbox.event.User", groupId = "user")
  public void consume(@Header("id") String msgId, InBox inbox) {
    if (inbox.getEventType() == "UserDelete") {
      try {
        // Get removed user info
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoRemoved userInfo =
            objectMapper.readValue(inbox.getPayload(), UserInfoRemoved.class);

        // Remove all stores and products owned by deleted user
        managementService.deleteStoreProudctByRemovedUser(userInfo);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
