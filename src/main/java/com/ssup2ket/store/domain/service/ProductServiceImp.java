package com.ssup2ket.store.domain.service;

import com.ssup2ket.store.domain.model.ProductInfo;
import com.ssup2ket.store.domain.repository.ProductInfoRepository;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImp implements ProductService {
  @Autowired private ProductInfoRepository productInfoPrimaryRepo;
  @Autowired private ProductInfoRepository productInfoSecondaryRepo;

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
    return productInfoPrimaryRepo.save(productInfo);
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
    productInfoPrimaryRepo.deleteById(productId);
  }

  @Override
  @Transactional("secondaryTransactionManager")
  public int getProductQuantity(UUID storeId, UUID productId) {
    return 0;
  }

  @Override
  @Transactional
  public void updateProductQuantity(UUID storeId, int productQuantity) {
    return;
  }

  @Override
  @Transactional
  public int increaseProductQuantity(UUID storeId, UUID productId, long increment) {
    return 0;
  }

  @Override
  @Transactional
  public int decreaseProductQuantity(UUID storeId, UUID productId, long decrement) {
    return 0;
  }
}
