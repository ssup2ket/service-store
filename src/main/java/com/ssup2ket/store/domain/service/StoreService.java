package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.StoreInfo;
import java.util.List;
import java.util.UUID;

public interface StoreService {
  public List<StoreInfo> listStoreInfos(int page, int limit);

  public List<StoreInfo> listStoreInfosByName(String name, int page, int size);

  public StoreInfo createStoreInfo(StoreInfo storeInfo);

  public StoreInfo getStoreInfo(UUID storeId);

  public void updateStoreInfo(StoreInfo storeInfo);

  public void deleteStoreInfo(UUID storeId);
}
