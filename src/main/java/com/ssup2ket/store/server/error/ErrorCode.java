package com.ssup2ket.store.server.error;

public class ErrorCode {
  // Resource
  private static final String RESOURCE_INVEN = "_INVENTORY";
  private static final String RESOURCE_PRODUCT = "_PRODUCT";

  // Common
  public static final String BAD_REQUEST = "BAD_REQUEST";
  public static final String UNAUTHORIZED = "UNAUTHORIZED";
  public static final String INTERNAl_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

  // Resource not found
  public static final String NOT_FOUND = "NOT_FOUND";
  public static final String NOT_FOUND_INVEN = NOT_FOUND + RESOURCE_INVEN;
  public static final String NOT_FOUND_PRODUCT = NOT_FOUND + RESOURCE_PRODUCT;

  // Resource conflict
  public static final String CONFLICT = "CONFLICT";
  public static final String CONFLICT_INVEN = CONFLICT + RESOURCE_INVEN;
  public static final String CONFLICT_PRODUCT = CONFLICT + RESOURCE_PRODUCT;
}
