package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.model.UserInfoRemoved;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.StoreInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.UserInfoRemovedPrimaryRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagementServiceImp implements ManagementService {

  @Autowired private StoreInfoPrimaryRepo storeInfoPrimaryRepo;
  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;
  @Autowired private UserInfoRemovedPrimaryRepo userInfoRemovedPrimaryRepo;

  @Override
  @Transactional
  public void deleteStoreProudctByRemovedUser(UserInfoRemoved userInfo) {
    // Create removed userinfo to prevent duplicate action
    userInfoRemovedPrimaryRepo.save(userInfo);

    // Delete all products ownedd by deleted user
    List<StoreInfo> storeList = storeInfoPrimaryRepo.findByUserId(userInfo.getId());
    for (StoreInfo storeInfo : storeList) {
      productInfoPrimaryRepo.deleteByStoreId(storeInfo.getId());
    }

    // Delete all stores owned by deleted user
    storeInfoPrimaryRepo.deleteByUserId(userInfo.getId());
  }
}
