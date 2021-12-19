package com.ssup2ket.store.domain.service;

import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.domain.entity.Inbox;
import com.ssup2ket.store.domain.entity.Outbox;
import com.ssup2ket.store.domain.entity.ProductInfo;
import com.ssup2ket.store.domain.repository.InboxPrimaryRepo;
import com.ssup2ket.store.domain.repository.OutboxPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoPrimaryRepo;
import com.ssup2ket.store.domain.repository.ProductInfoSecondaryRepo;
import com.ssup2ket.store.domain.vo.ProductOrder;
import com.ssup2ket.store.pkg.tracing.SpanContext;
import com.ssup2ket.store.server.error.ProductNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductServiceImp implements ProductService {
  private static final String AGGREGATE_TYPE_PRODUCT = "Product";
  private static final String EVENT_TYPE_PRODUCT_CREATED = "ProductCreated";
  private static final String EVENT_TYPE_PRODUCT_DELETED = "ProductDeleted";
  private static final String EVENT_TYPE_PRODUCT_INCREASED = "ProductIncreased";
  private static final String EVENT_TYPE_PRODUCT_INCREASED_ERROR = "ProductIncreasedError";
  private static final String EVENT_TYPE_PRODUCT_DECREASED = "ProductDecreased";
  private static final String EVENT_TYPE_PRODUCT_DECREASED_ERROR = "ProductDecreasedError";

  @Autowired private ProductInfoPrimaryRepo productInfoPrimaryRepo;
  @Autowired private ProductInfoSecondaryRepo productInfoSecondaryRepo;
  @Autowired private OutboxPrimaryRepo outboxPrimaryRepo;
  @Autowired private InboxPrimaryRepo inboxPrimaryRepo;
  @Autowired private Tracer tracer;

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

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox for product creation event
      ObjectMapper jsonMapper = new ObjectMapper();
      String productInfoJson = jsonMapper.writeValueAsString(productInfo);
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productInfo.getId().toString(),
              EVENT_TYPE_PRODUCT_CREATED,
              productInfoJson,
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // return
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

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox for product deletion event
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productInfo.getId().toString(),
              EVENT_TYPE_PRODUCT_DELETED,
              productInfo.toJsonString(),
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
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
  public void increaseProductQuantityMq(Inbox inbox) {
    try {
      increaseProductQuantityMqTx(inbox);
    } catch (DuplicateKeyException e) {
      // Ignore dup exception
      log.info("Occur DB duplicate key exception. Ignore it");
    } catch (Exception e) {
      log.error("Increase product error", e);
      increaseProductQuantityMqError(inbox);
    }
  }

  @Transactional
  public void increaseProductQuantityMqTx(Inbox inbox) {
    // Get order info from inbox
    ProductOrder productOrder = new ProductOrder(inbox.getPayload());

    // Save inbox to prevent duplicate action
    if (inboxPrimaryRepo.findById(productOrder.getId()).isPresent()) {
      throw new DuplicateKeyException("duplicated inbox");
    }
    inboxPrimaryRepo.save(inbox);

    // Increment
    productInfoPrimaryRepo.incraseQuantity(productOrder.getId(), productOrder.getCount());

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox to publish product increment event
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productOrder.getId().toString(),
              EVENT_TYPE_PRODUCT_INCREASED,
              "",
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public void increaseProductQuantityMqError(Inbox inbox) {
    // Get order info from inbox
    ProductOrder productOrder = new ProductOrder(inbox.getPayload());

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox to public product increment error event
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productOrder.getId().toString(),
              EVENT_TYPE_PRODUCT_INCREASED_ERROR,
              "",
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Transactional
  public int decreaseProductQuantity(UUID storeId, UUID productId, int decrement) {
    productInfoPrimaryRepo.decraseQuantity(productId, decrement);
    ProductInfo productInfo = productInfoPrimaryRepo.getById(productId);
    if (productInfo.getQuantity() < 0) {
      throw new RuntimeException("insuffcient product quantity");
    }
    return productInfo.getQuantity();
  }

  @Override
  public void decreaseProductQuantityMq(Inbox inbox) {
    try {
      decreaseProductQuantityMqTx(inbox);
    } catch (DuplicateKeyException e) {
      // Ignore dup exception
      log.info("Occur DB duplicate key exception. Ignore it");
    } catch (Exception e) {
      log.error("Decrese product error", e);
      decreaseProductQuantityMqError(inbox);
    }
  }

  @Transactional
  public void decreaseProductQuantityMqTx(Inbox inbox) {
    // Get order info from inbox
    ProductOrder productOrder = new ProductOrder(inbox.getPayload());

    // Check and Save inbox to prevent duplicate action
    if (inboxPrimaryRepo.findById(inbox.getId()).isPresent()) {
      throw new DuplicateKeyException("duplicated inbox");
    }
    inboxPrimaryRepo.save(inbox);

    // Decrement
    productInfoPrimaryRepo.decraseQuantity(productOrder.getId(), productOrder.getCount());
    ProductInfo productInfo = productInfoPrimaryRepo.getById(productOrder.getId());
    if (productInfo.getQuantity() < 0) {
      throw new RuntimeException("insufficient product quantity");
    }

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox to publish product decrement event
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productInfo.getId().toString(),
              EVENT_TYPE_PRODUCT_DECREASED,
              productInfo.toJsonString(),
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public void decreaseProductQuantityMqError(Inbox inbox) {
    // Get order info from inbox
    ProductOrder productOrder = new ProductOrder(inbox.getPayload());

    try {
      // Get span context as JSON
      String spanContextJson = SpanContext.GetSpanContextAsJson(tracer.currentSpan());

      // Create outbox to publish product decrement error event
      Outbox outbox =
          new Outbox(
              null,
              AGGREGATE_TYPE_PRODUCT,
              productOrder.getId().toString(),
              EVENT_TYPE_PRODUCT_DECREASED_ERROR,
              "",
              spanContextJson);
      outboxPrimaryRepo.save(outbox);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
