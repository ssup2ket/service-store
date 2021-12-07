package com.ssup2ket.store.pkg.tracing;

import brave.Span;
import brave.Tracer;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(SleuthWebProperties.TRACING_FILTER_ORDER + 1)
public class SpanIdResponse extends GenericFilterBean {
  private final Tracer tracer;

  SpanIdResponse(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Span currentSpan = this.tracer.currentSpan();
    if (currentSpan == null) {
      chain.doFilter(request, response);
      return;
    }

    // Copy trace ID to response
    ((HttpServletResponse) response)
        .addHeader("X-B3-TraceId", currentSpan.context().traceIdString());
    chain.doFilter(request, response);
  }
}
