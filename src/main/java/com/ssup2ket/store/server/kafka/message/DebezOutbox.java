package com.ssup2ket.store.server.kafka.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebezOutbox {
  private String schema;
  private String payload;
  private String eventType;
}
