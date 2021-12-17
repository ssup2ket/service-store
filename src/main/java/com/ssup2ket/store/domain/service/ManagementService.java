package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.Inbox;

public interface ManagementService {
  public void deleteStoreProudctByRemovedUserInbox(Inbox inbox);
}
