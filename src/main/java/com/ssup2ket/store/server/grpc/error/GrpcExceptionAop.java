package com.ssup2ket.store.server.grpc.error;

import io.grpc.stub.StreamObserver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class GrpcExceptionAop {

  @Around("execution(public * com.ssup2ket.store.server.grpc.service.*.*(..))")
  public void catchException(ProceedingJoinPoint joinPoint) {
    try {
      joinPoint.proceed();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, ((StreamObserver<?>) joinPoint.getArgs()[1]));
    }
  }
}
