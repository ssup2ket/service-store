package com.ssup2ket.store.domain.repository;

import com.ssup2ket.store.domain.model.StoreInfo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreInfoPrimaryRepo extends JpaRepository<StoreInfo, UUID> {
  public List<StoreInfo> findByName(String name, Pageable pageable);
}
