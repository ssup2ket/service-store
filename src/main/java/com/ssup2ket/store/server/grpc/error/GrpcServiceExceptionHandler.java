package com.ssup2ket.store.server.grpc.error;

import com.ssup2ket.store.server.error.ErrorCode;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcServiceExceptionHandler {
  @GrpcExceptionHandler(StoreNotFoundException.class)
  public Status handleStoreNotFoundException(StoreNotFoundException e) {
    return Status.NOT_FOUND.withDescription(ErrorCode.NOT_FOUND_STORE);
  }

  @GrpcExceptionHandler(ProductNotFoundException.class)
  public Status handleProductNotFoundException(ProductNotFoundException e) {
    return Status.NOT_FOUND.withDescription(ErrorCode.NOT_FOUND_STORE);
  }
}
