package com.ssup2ket.store.server.http.error;

import com.ssup2ket.store.server.error.ErrorCode;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class HttpErrorAttributes extends DefaultErrorAttributes {
  // Override for BaseExecptionHandler
  @Override
  public Map<String, Object> getErrorAttributes(WebRequest request, ErrorAttributeOptions options) {
    return HttpErrorBuilder.getResponse(ErrorCode.INTERNAl_SERVER_ERROR, "");
  }
}
