package com.ssup2ket.store.server.grpc.service;

import com.ssup2ket.store.domain.model.ProductInfo;
import com.ssup2ket.store.domain.service.ProductService;
import com.ssup2ket.store.proto.*;
import com.ssup2ket.store.proto.ProductGrpc.ProductImplBase;
import com.ssup2ket.store.server.grpc.error.GrpcExceptionHandler;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GRpcService
public class ProductGrpc extends ProductImplBase {
  @Autowired ProductService service;

  @Override
  public void listProduct(ProductListReq request, StreamObserver<ProductListRes> responseObserver) {
    try {
      ProductListRes res =
          convertProductInfoListResToRes(
              service.listProductInfos(
                  UUID.fromString(request.getStoreId()),
                  request.getOffset() / request.getLimit(),
                  request.getLimit()));
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, responseObserver);
    }
  }

  @Override
  public void createProduct(
      ProductCreateReq request, StreamObserver<ProductInfoRes> responseObserver) {
    ProductInfo req = convertProductCreateReqToModel(request);
    ProductInfo res = service.createProductInfo(req);
    responseObserver.onNext(convertProductInfoToRes(res));
    responseObserver.onCompleted();
  }

  @Override
  public void getProduct(ProductIdReq request, StreamObserver<ProductInfoRes> responseObserver) {
    ProductInfo storeInfo =
        service.getProductInfo(
            UUID.fromString(request.getStoreId()), UUID.fromString(request.getId()));
    responseObserver.onNext(convertProductInfoToRes(storeInfo));
    responseObserver.onCompleted();
  }

  @Override
  public void updateProduct(ProductUpdateReq request, StreamObserver<Empty> responseObserver) {
    ProductInfo reqProductInfo = convertProductUpdateReqToModel(request);
    service.updateProductInfo(reqProductInfo);
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void deleteProduct(ProductIdReq request, StreamObserver<Empty> responseObserver) {
    service.deleteProductInfo(
        UUID.fromString(request.getStoreId()), UUID.fromString(request.getId()));
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  public ProductInfo convertProductCreateReqToModel(ProductCreateReq request) {
    return new ProductInfo(
        null,
        UUID.fromString(request.getStoreId()),
        request.getName(),
        request.getDescription(),
        request.getQuantity());
  }

  public ProductInfo convertProductUpdateReqToModel(ProductUpdateReq request) {
    return new ProductInfo(
        UUID.fromString(request.getId()),
        UUID.fromString(request.getStoreId()),
        request.getName(),
        request.getDescription(),
        request.getQuantity());
  }

  public ProductInfoRes convertProductInfoToRes(ProductInfo storeInfo) {
    ProductInfoRes.Builder builder =
        ProductInfoRes.newBuilder()
            .setId(storeInfo.getId().toString())
            .setName(storeInfo.getName())
            .setDescription(storeInfo.getDescription())
            .setStoreId(storeInfo.getStoreId().toString());
    return builder.build();
  }

  public ProductListRes convertProductInfoListResToRes(List<ProductInfo> storeInfoList) {
    // Make up storetory info response list
    ProductListRes.Builder builder = ProductListRes.newBuilder();
    Iterator<ProductInfo> iter = storeInfoList.iterator();
    while (iter.hasNext()) {
      builder.addProducts(convertProductInfoToRes(iter.next()));
    }
    return builder.build();
  }
}
