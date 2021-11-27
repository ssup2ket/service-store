package com.ssup2ket.store.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.domain.model.Outbox;
import com.ssup2ket.store.domain.model.ProductInfo;
import com.ssup2ket.store.domain.repository.OutboxPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoSecondaryRepo;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImp implements ProductService {
  private static final String aggregateProuductType = "Product";

  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;
  @Autowired private ProductInfoSecondaryRepo productInfoSecondaryRepo;
  @Autowired private OutboxPrimaryRepo outboxPrimaryRepo;

  @Override
  @Transactional("secondaryTransactionManager")
  public List<ProductInfo> listProductInfos(UUID storeId, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return productInfoSecondaryRepo.findAll(pageRequest).getContent();
  }

  @Override
  @Transactional("secondaryTransactionManager")
  public List<ProductInfo> listProductInfosByName(UUID storeId, String name, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return productInfoSecondaryRepo.findByName(name, pageRequest);
  }

  @Override
  @Transactional
  public ProductInfo createProductInfo(ProductInfo productInfo) {
    // Create product
    productInfo = productInfoPrimaryRepo.save(productInfo);

    // Create outbox for product creation event
    try {
      ObjectMapper jsonMapper = new ObjectMapper();
      String productInfoJson = jsonMapper.writeValueAsString(productInfo);
      Outbox outbox =
          new Outbox(
              null,
              aggregateProuductType,
              productInfo.getId().toString(),
              "ProductCreate",
              productInfoJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return productInfo;
  }

  @Override
  @Transactional("secondaryTransactionManager")
  public ProductInfo getProductInfo(UUID storeId, UUID productId) {
    return productInfoSecondaryRepo
        .findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }

  @Override
  @Transactional
  public void updateProductInfo(ProductInfo productInfo) {
    productInfoPrimaryRepo.save(productInfo);
  }

  @Override
  @Transactional
  public void deleteProductInfo(UUID storeId, UUID productId) {
    // Get and delete product
    ProductInfo productInfo = productInfoPrimaryRepo.getById(productId);
    productInfoPrimaryRepo.deleteById(productId);

    // Create outbox for product deletion event
    try {
      ObjectMapper jsonMapper = new ObjectMapper();
      String productInfoJson = jsonMapper.writeValueAsString(productInfo);
      Outbox outbox =
          new Outbox(
              null,
              aggregateProuductType,
              productInfo.getId().toString(),
              "ProductDelete",
              productInfoJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  @Transactional
  public int increaseProductQuantity(UUID storeId, UUID productId, int increment) {
    productInfoPrimaryRepo.incraseQuantity(productId, increment);
    ProductInfo productInfo = productInfoPrimaryRepo.getById(productId);
    return productInfo.getQuantity();
  }

  @Override
  @Transactional
  public int decreaseProductQuantity(UUID storeId, UUID productId, int decrement) {
    productInfoPrimaryRepo.decraseQuantity(productId, decrement);
    ProductInfo productInfo = productInfoPrimaryRepo.getById(productId);
    return productInfo.getQuantity();
  }
}
