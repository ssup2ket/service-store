package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.entity.Inbox;

public interface ManagementService {
  public void deleteStoreProudctByDeletedUserMq(Inbox inbox);
}
