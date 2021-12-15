package com.ssup2ket.store.server.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

@Slf4j
public class ConsumerAop {
  @Around("execution(* com.ssup2ket.store.server.kafka.consumer.*.*(..))")
  public Object LoggingAccess(ProceedingJoinPoint jointPoint) throws Throwable {
    // Logging access
    long start = System.currentTimeMillis();
    try {
      return jointPoint.proceed(jointPoint.getArgs());
    } finally {
      long end = System.currentTimeMillis();
      log.info(
          "method{} args{} duration:{}ms",
          jointPoint.getSignature().toShortString(),
          jointPoint.getArgs(),
          end - start);
    }
  }
}
