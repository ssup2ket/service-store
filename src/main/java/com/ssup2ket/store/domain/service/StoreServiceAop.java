package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.repository.StoreInfoPrimaryRepo;
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
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class StoreServiceAop {
  @Autowired private StoreInfoPrimaryRepo storeInfoPrimaryRepo;

  @Before(
      "execution(*"
          + " com.ssup2ket.store.domain.service.StoreServiceImp.updateStoreInfo(..)) ||"
          + " execution(*"
          + " com.ssup2ket.store.domain.service.StoreServiceImp.deleteStoreInfo(..))")
  private void validateStoreFromPrimaryDB(JoinPoint joinPoint) {
    // Get store UUID
    UUID storeId;
    if (joinPoint.getArgs()[0] instanceof UUID) {
      storeId = (UUID) joinPoint.getArgs()[1];
    } else if (joinPoint.getArgs()[0] instanceof StoreInfo) {
      storeId = ((StoreInfo) joinPoint.getArgs()[0]).getId();
    } else {
      throw new IllegalArgumentException("Wrong product UUID");
    }

    // Get store info
    storeInfoPrimaryRepo.findById(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));
  }
}
