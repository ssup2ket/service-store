package com.ssup2ket.store.server.http.error;

import com.ssup2ket.store.server.error.ErrorCode;
import com.ssup2ket.store.server.error.ErrorMessage;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class HttpControllerExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = {StoreNotFoundException.class})
  private ResponseEntity<Object> handleStoreNotFoundException(
      StoreNotFoundException exception, WebRequest request) {
    return handleExceptionInternal(
        exception,
        HttpErrorResponseBodyBuilder.getResponseAsMap(
            ErrorCode.NOT_FOUND_STORE, ErrorMessage.NOT_FOUND_STORE),
        new HttpHeaders(),
        HttpStatus.NOT_FOUND,
        request);
  }

  @ExceptionHandler(value = {ProductNotFoundException.class})
  private ResponseEntity<Object> handleProductNotFoundException(
      ProductNotFoundException exception, WebRequest request) {
    return handleExceptionInternal(
        exception,
        HttpErrorResponseBodyBuilder.getResponseAsMap(
            ErrorCode.NOT_FOUND_PRODUCT, ErrorMessage.NOT_FOUND_PRODUCT),
        new HttpHeaders(),
        HttpStatus.NOT_FOUND,
        request);
  }
}