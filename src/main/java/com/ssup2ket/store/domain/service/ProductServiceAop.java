package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.entity.Inbox;
import com.ssup2ket.store.domain.entity.ProductInfo;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoSecondaryRepo;
import com.ssup2ket.store.domain.repository.StoreInfoPrimaryRepo;
import com.ssup2ket.store.domain.vo.ProductOrder;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class ProductServiceAop {
  @Autowired private StoreInfoPrimaryRepo storeInfoSecondaryRepo;
  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;
  @Autowired private ProductInfoSecondaryRepo productInfoSecondaryRepo;

  @Before("execution(* com.ssup2ket.store.domain.service.ProductServiceImp.*(..))")
  private void validateStoreFromSecondaryDB(JoinPoint joinPoint) {
    // Get store UUID
    UUID storeId;
    if (joinPoint.getArgs()[0] instanceof UUID) {
      storeId = (UUID) joinPoint.getArgs()[0];
    } else if (joinPoint.getArgs()[0] instanceof ProductInfo) {
      storeId = ((ProductInfo) joinPoint.getArgs()[0]).getStoreId();
    } else if (joinPoint.getArgs()[0] instanceof Inbox) {
      Inbox inbox = ((Inbox) joinPoint.getArgs()[0]);
      ProductOrder productOrder = new ProductOrder(inbox.getPayload());
      storeId = productOrder.getStoreId();
    } else {
      throw new IllegalArgumentException("Wrong store ID");
    }

    // Get store info
    storeInfoSecondaryRepo.findById(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));
  }

  @Before(
      "execution(*"
          + " com.ssup2ket.store.domain.service.ProductServiceImp.updateProductInfo(..)) ||"
          + " execution(*"
          + " com.ssup2ket.store.domain.service.ProductServiceImp.deleteProductInfo(..))")
  private void validateProductFromPrimaryDB(JoinPoint joinPoint) {
    // Get product UUID
    UUID productId;
    if (joinPoint.getArgs()[1] instanceof UUID) {
      productId = (UUID) joinPoint.getArgs()[1];
    } else if (joinPoint.getArgs()[1] instanceof ProductInfo) {
      productId = ((ProductInfo) joinPoint.getArgs()[0]).getStoreId();
    } else {
      throw new IllegalArgumentException("Wrong product UUID");
    }

    // Get product info
    productInfoPrimaryRepo
        .findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }

  @Before("execution(* com.ssup2ket.store.domain.service.ProductServiceImp.*Quantity(..))")
  private void validateProductFromSecondaryDB(JoinPoint joinPoint) {
    // Get product UUID
    UUID productId;
    if (joinPoint.getArgs()[1] instanceof UUID) {
      productId = (UUID) joinPoint.getArgs()[1];
    } else if (joinPoint.getArgs()[1] instanceof ProductInfo) {
      productId = ((ProductInfo) joinPoint.getArgs()[1]).getStoreId();
    } else {
      throw new IllegalArgumentException("Wrong product UUID");
    }

    // Get product info
    productInfoSecondaryRepo
        .findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }
}
