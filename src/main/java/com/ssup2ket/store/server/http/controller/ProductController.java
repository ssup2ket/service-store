package com.ssup2ket.store.server.http.controller;

import com.ssup2ket.store.domain.model.ProductInfo;
import com.ssup2ket.store.domain.service.ProductService;
import com.ssup2ket.store.server.http.dto.ProductInfoListRes;
import com.ssup2ket.store.server.http.dto.ProductInfoReq;
import com.ssup2ket.store.server.http.dto.ProductInfoRes;
import com.ssup2ket.store.server.http.dto.ProductQuantityReq;
import com.ssup2ket.store.server.http.dto.ProductQuantityRes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Validated
public class ProductController {
  private final String uuidRegExp =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

  @Autowired private ProductService service;
  @Autowired private ModelMapper modelMapper;

  @GetMapping("/stores/{storeId}/products")
  ProductInfoListRes listProduct(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "50") int limit) {
    return convertProductInfoListResToDto(
        service.listProductInfos(UUID.fromString(storeId), offset / limit, limit));
  }

  @PostMapping("/stores/{storeId}/products")
  ProductInfoRes createProduct(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @RequestBody @Valid ProductInfoReq request) {
    // Set request
    ProductInfo req = convertProductInfoReqToModel(request);
    req.setStoreId(UUID.fromString(storeId));

    // Create product
    ProductInfo resProductInfo = service.createProductInfo(req);
    return convertProductInfoResToDto(resProductInfo);
  }

  @GetMapping("/stores/{storeId}/products/{productId}")
  ProductInfoRes getProduct(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId) {
    ProductInfo res = service.getProductInfo(UUID.fromString(storeId), UUID.fromString(productId));
    return convertProductInfoResToDto(res);
  }

  @PutMapping("/stores/{storeId}/products/{productId}")
  void updateProduct(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId,
      @RequestBody @Valid ProductInfoReq request) {
    ProductInfo req = convertProductInfoReqToModel(request);
    req.setStoreId(UUID.fromString(storeId));
    req.setId(UUID.fromString(productId));
    service.updateProductInfo(req);
  }

  @DeleteMapping("/stores/{storeId}/products/{productId}")
  void deleteProduct(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId) {
    service.deleteProductInfo(UUID.fromString(storeId), UUID.fromString(productId));
  }

  @GetMapping("/stores/{storeId}/products/{productId}/quantity")
  ProductQuantityRes getProductQuantity(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId) {
    return convertProductQuantityResToDto(
        storeId, service.getProductQuantity(UUID.fromString(storeId), UUID.fromString(productId)));
  }

  @PutMapping("/stores/{storeId}/products/{productId}/quantity")
  void updateProductQuantity(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId,
      @RequestBody @Valid ProductQuantityReq request) {
    service.updateProductQuantity(UUID.fromString(storeId), request.getQuantity());
  }

  @PostMapping("/stores/{storeId}/products/{productId}/quantity/increase")
  ProductQuantityRes increaseProductQuantity(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId,
      @RequestBody @Valid ProductQuantityReq request) {
    return convertProductQuantityResToDto(
        productId,
        service.increaseProductQuantity(
            UUID.fromString(storeId), UUID.fromString(productId), request.getQuantity()));
  }

  @PostMapping("/stores/{storeId}/products/{productId}/quantity/decrease")
  ProductQuantityRes decreaseProductQuantity(
      @PathVariable @Pattern(regexp = uuidRegExp) String storeId,
      @PathVariable @Pattern(regexp = uuidRegExp) String productId,
      @RequestBody @Valid ProductQuantityReq request) {
    return convertProductQuantityResToDto(
        storeId,
        service.decreaseProductQuantity(
            UUID.fromString(storeId), UUID.fromString(productId), request.getQuantity()));
  }

  public ProductInfo convertProductInfoReqToModel(ProductInfoReq request) {
    return modelMapper.map(request, ProductInfo.class);
  }

  public ProductInfoRes convertProductInfoResToDto(ProductInfo productInfo) {
    return modelMapper.map(productInfo, ProductInfoRes.class);
  }

  public ProductInfoListRes convertProductInfoListResToDto(List<ProductInfo> productInfoList) {
    // Make up product info response list
    List<ProductInfoRes> productInfoResList = new ArrayList<>();
    Iterator<ProductInfo> iter = productInfoList.iterator();
    while (iter.hasNext()) {
      productInfoResList.add(convertProductInfoResToDto(iter.next()));
    }

    // Return response
    return new ProductInfoListRes(productInfoList);
  }

  public ProductQuantityRes convertProductQuantityResToDto(String productId, int productQuantity) {
    return new ProductQuantityRes(productId, productQuantity);
  }
}
