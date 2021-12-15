package com.ssup2ket.store.server.kafka.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DebezOutbox {
  private String schema;
  private String payload;
  private String eventType;
}
