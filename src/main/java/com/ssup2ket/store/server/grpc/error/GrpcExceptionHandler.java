package com.ssup2ket.store.server.grpc.error;

import com.ssup2ket.store.server.error.ProductNotFoundException;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GrpcExceptionHandler {
  public static <V> void handler(Throwable t, StreamObserver<V> responseObserver) {
    if (t instanceof StoreNotFoundException) {
      t = new StatusRuntimeException(Status.NOT_FOUND.withDescription(t.getMessage()));
    } else if (t instanceof ProductNotFoundException) {
      t = new StatusRuntimeException(Status.NOT_FOUND.withDescription(t.getMessage()));
    }

    responseObserver.onError(t);
  }
}
