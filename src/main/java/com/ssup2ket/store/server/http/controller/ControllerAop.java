package com.ssup2ket.store.server.http.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Log4j2
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class ControllerAop {
  @Around("execution(* com.ssup2ket.store.server.http.controller.*.*(..))")
  public Object LoggingAccess(ProceedingJoinPoint jointPoint) throws Throwable {
    // Get request from context
    ServletRequestAttributes servletRequestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = servletRequestAttributes.getRequest();
    HttpServletResponse response = servletRequestAttributes.getResponse();

    // Logging access
    long start = System.currentTimeMillis();
    try {
      return jointPoint.proceed(jointPoint.getArgs());
    } finally {
      long end = System.currentTimeMillis();
      log.info(
          "method:{} url:{} client_ip:{} status:{} duration:{}ms",
          request.getMethod(),
          request.getRequestURI(),
          request.getRemoteHost(),
          response.getStatus(),
          end - start);
    }
  }
}
