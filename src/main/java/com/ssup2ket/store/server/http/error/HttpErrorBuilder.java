package com.ssup2ket.store.server.http.error;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpErrorBuilder {
  public static Map<String, Object> getResponse(String code, String requestId) {
    Map<String, Object> result = new HashMap<>();
    result.put("code", code);
    result.put("requestId", requestId);
    return result;
  }
}
