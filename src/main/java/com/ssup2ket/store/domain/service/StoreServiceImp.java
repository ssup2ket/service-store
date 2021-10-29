package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.repository.StoreInfoRepository;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImp implements StoreService {
  @Autowired private StoreInfoRepository storeInfoPrimaryRepo;
  @Autowired private StoreInfoRepository storeInfoSecondaryRepo;

  @Override
  public List<StoreInfo> listStoreInfos(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return storeInfoSecondaryRepo.findAll(pageRequest).getContent();
  }

  @Override
  public List<StoreInfo> listStoreInfosByName(String name, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return storeInfoSecondaryRepo.findByName(name, pageRequest);
  }

  @Override
  @Transactional
  public StoreInfo createStoreInfo(StoreInfo inventoryInfo) {
    return storeInfoPrimaryRepo.save(inventoryInfo);
  }

  @Override
  @Transactional("secondaryTransactionManager")
  public StoreInfo getStoreInfo(UUID storeId) {
    return storeInfoSecondaryRepo
        .findById(storeId)
        .orElseThrow(() -> new StoreNotFoundException(storeId));
  }

  @Override
  @Transactional
  public void updateStoreInfo(StoreInfo storeInfo) {
    storeInfoPrimaryRepo.save(storeInfo);
  }

  @Override
  @Transactional
  public void deleteStoreInfo(UUID storeId) {
    storeInfoPrimaryRepo.deleteById(storeId);
  }
}
