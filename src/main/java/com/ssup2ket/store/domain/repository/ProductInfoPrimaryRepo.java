package com.ssup2ket.store.domain.repository;

import com.ssup2ket.store.domain.model.ProductInfo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInfoPrimaryRepo extends JpaRepository<ProductInfo, UUID> {
  public List<ProductInfo> findByName(String name, Pageable pageable);

  @Modifying
  @Query(value = "UPDATE ProductInfo p SET p.quantity = p.quantity + :increment WHERE id = :id")
  public int incraseQuantity(@Param("id") UUID id, @Param("increment") int increment);

  @Modifying
  @Query(value = "UPDATE ProductInfo p SET p.quantity = p.quantity - :decrement WHERE id = :id")
  public int decraseQuantity(@Param("id") UUID id, @Param("decrement") int decrement);
}
