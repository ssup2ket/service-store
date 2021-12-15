package com.ssup2ket.store.server.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.server.kafka.message.DebezOutbox;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class ConsumerHelper {
  public static UUID getMsgUuidFromStringId(String headerId) {
    ByteBuffer bb = ByteBuffer.wrap(Base64.getDecoder().decode(headerId));
    return new UUID(bb.getLong(), bb.getLong());
  }

  public static DebezOutbox getDebezOutboxFromMsg(String msgId, String msg)
      throws JsonMappingException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    DebezOutbox outbox = objectMapper.readValue(msg, DebezOutbox.class);
    outbox.setId(getMsgUuidFromStringId(msgId));
    return outbox;
  }
}
