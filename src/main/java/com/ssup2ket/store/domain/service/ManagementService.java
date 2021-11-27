package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.UserInfoRemoved;

public interface ManagementService {
  public void deleteStoreProudctByRemovedUser(UserInfoRemoved userInfo);
}
