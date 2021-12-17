package com.ssup2ket.store.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.domain.model.Inbox;
import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.model.UserInfo;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.StoreInfoPrimaryRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagementServiceImp implements ManagementService {

  @Autowired private StoreInfoPrimaryRepo storeInfoPrimaryRepo;
  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;

  @Override
  @Transactional
  public void deleteStoreProudctByRemovedUserMq(Inbox inbox) {
    // Get userinfo from inbox
    UserInfo userInfo;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      userInfo = objectMapper.readValue(inbox.getPayload(), UserInfo.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Save inbox to prevent duplicate action
    // Saving inbox isn't necessary action bacause delete action could be repeatedly performed
    // inboxPrimaryRepo.save(inbox);

    // Delete all products ownedd by deleted user
    List<StoreInfo> storeList = storeInfoPrimaryRepo.findByUserId(userInfo.getId());
    for (StoreInfo storeInfo : storeList) {
      productInfoPrimaryRepo.deleteByStoreId(storeInfo.getId());
    }

    // Delete all stores owned by deleted user
    storeInfoPrimaryRepo.deleteByUserId(userInfo.getId());
  }
}
