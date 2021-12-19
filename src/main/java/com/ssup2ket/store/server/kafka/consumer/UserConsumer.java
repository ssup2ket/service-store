package com.ssup2ket.store.server.kafka.consumer;

import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import com.ssup2ket.store.domain.entity.Inbox;
import com.ssup2ket.store.domain.service.ManagementService;
import com.ssup2ket.store.pkg.tracing.SpanContext;
import com.ssup2ket.store.server.kafka.message.DebezOutbox;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserConsumer {
  private static final String AGGREGATE_TYPE_USER = "User";
  private static final String EVENT_TYPE_USER_DELETED = "UserDeleted";

  @Autowired private ManagementService managementService;
  @Autowired private Tracer tracer;

  @KafkaListener(
      topics = "#{${spring.kafka.topic.prefix}}-ssup2ket-auth-outbox-User",
      groupId = "#{${spring.kafka.groupid.prefix}}-ssup2ket-store-user")
  public void consume(
      @Header("id") String msgId,
      @Header("spanContext") String spanContextJson,
      @Payload String msg,
      Acknowledgment ack) {

    try {
      // Get msg info
      UUID msgUuid = ConsumerHelper.getMsgUuidFromStringId(msgId);
      DebezOutbox outbox = ConsumerHelper.getDebezOutboxFromMsg(msgId, msg);
      log.info(
          "User Consumer id:{} eventType:{} event:{}",
          outbox.getId().toString(),
          outbox.getPayload().getEventType(),
          outbox.getPayload().getEvent());

      if (outbox.getPayload().getEventType().equals(EVENT_TYPE_USER_DELETED)) {
        // Set span context
        TraceContextOrSamplingFlags spanContext =
            SpanContext.GetSpanContextFromJson(spanContextJson);
        tracer.nextSpan(spanContext);

        // Call delete service
        Inbox inbox =
            new Inbox(
                msgUuid,
                AGGREGATE_TYPE_USER,
                EVENT_TYPE_USER_DELETED,
                outbox.getPayload().getEvent(),
                spanContextJson);
        managementService.deleteStoreProudctByDeletedUserMq(inbox);
      }
    } catch (Exception e) {
      log.error("User consumer exception", e);
      throw new RuntimeException(e);
    }
    ack.acknowledge();
  }
}
