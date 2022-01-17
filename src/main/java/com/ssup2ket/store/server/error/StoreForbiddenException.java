package com.ssup2ket.store.server.error;

import java.util.UUID;

public class StoreForbiddenException extends RuntimeException {
  public StoreForbiddenException(UUID storeUuid) {
    super("Store uuid " + storeUuid.toString());
  }
}
