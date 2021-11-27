package com.ssup2ket.store.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.domain.model.Outbox;
import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.repository.OutboxPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.StoreInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.StoreInfoSecondaryRepo;
import com.ssup2ket.store.pkg.auth.AccessToken;
import com.ssup2ket.store.server.error.StoreNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImp implements StoreService {
  private static final String aggregateStoreType = "Store";

  @Autowired private StoreInfoPrimaryRepo storeInfoPrimaryRepo;
  @Autowired private StoreInfoSecondaryRepo storeInfoSecondaryRepo;
  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;
  @Autowired private OutboxPrimaryRepo outboxPrimaryRepo;

  @Override
  @Transactional("secondaryTransactionManager")
  public List<StoreInfo> listStoreInfos(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return storeInfoSecondaryRepo.findAll(pageRequest).getContent();
  }

  @Override
  @Transactional("secondaryTransactionManager")
  public List<StoreInfo> listStoreInfosByName(String name, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    return storeInfoSecondaryRepo.findByName(name, pageRequest);
  }

  @Override
  @Transactional
  public StoreInfo createStoreInfo(StoreInfo storeInfo) {
    // Get user ID form access token and set user ID to store info
    AccessToken accessToken = (AccessToken) SecurityContextHolder.getContext().getAuthentication();
    storeInfo.setUserId(accessToken.getUserId());

    // Create store
    storeInfo = storeInfoPrimaryRepo.save(storeInfo);

    // Create outbox for store creation event
    try {
      ObjectMapper jsonMapper = new ObjectMapper();
      String storeInfoJson = jsonMapper.writeValueAsString(storeInfo);
      Outbox outbox =
          new Outbox(
              null, aggregateStoreType, storeInfo.getId().toString(), "StoreCreate", storeInfoJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return storeInfoPrimaryRepo.save(storeInfo);
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
    // Delete all products owned by store
    productInfoPrimaryRepo.deleteByStoreId(storeId);

    // Get and delete store
    StoreInfo storeInfo = storeInfoPrimaryRepo.getById(storeId);
    storeInfoPrimaryRepo.deleteById(storeId);

    // Create outbox for store deletion event
    try {
      ObjectMapper jsonMapper = new ObjectMapper();
      String storeInfoJson = jsonMapper.writeValueAsString(storeInfo);
      Outbox outbox =
          new Outbox(
              null, aggregateStoreType, storeInfo.getId().toString(), "StoreDelete", storeInfoJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteStoreInfoByOwnerID(String msgId, UUID ownerId) {}
}
