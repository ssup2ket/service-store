package com.ssup2ket.store.server.grpc.service;

import com.ssup2ket.store.domain.model.StoreInfo;
import com.ssup2ket.store.domain.service.StoreService;
import com.ssup2ket.store.proto.*;
import com.ssup2ket.store.proto.StoreGrpc.StoreImplBase;
import com.ssup2ket.store.server.grpc.error.GrpcExceptionHandler;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GRpcService
public class StoreGrpc extends StoreImplBase {
  @Autowired StoreService service;

  @Override
  public void listStore(StoreListReq request, StreamObserver<StoreListRes> responseObserver) {
    try {
      StoreListRes res =
          convertStoreInfoListResToRes(
              service.listStoreInfos(request.getOffset() / request.getLimit(), request.getLimit()));
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, responseObserver);
    }
  }

  @Override
  public void createStore(StoreCreateReq request, StreamObserver<StoreInfoRes> responseObserver) {
    try {
      StoreInfo req = convertStoreCreateReqToModel(request);
      StoreInfo res = service.createStoreInfo(req);
      responseObserver.onNext(convertStoreInfoToRes(res));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, responseObserver);
    }
  }

  @Override
  public void getStore(StoreIdReq request, StreamObserver<StoreInfoRes> responseObserver) {
    StoreInfo storeInfo = service.getStoreInfo(UUID.fromString(request.getId()));
    responseObserver.onNext(convertStoreInfoToRes(storeInfo));
    responseObserver.onCompleted();
  }

  @Override
  public void updateStore(StoreUpdateReq request, StreamObserver<Empty> responseObserver) {
    try {
      StoreInfo reqStoreInfo = convertStoreUpdateReqToModel(request);
      service.updateStoreInfo(reqStoreInfo);
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, responseObserver);
    }
  }

  @Override
  public void deleteStore(StoreIdReq request, StreamObserver<Empty> responseObserver) {
    try {
      service.deleteStoreInfo(UUID.fromString(request.getId()));
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    } catch (Throwable t) {
      GrpcExceptionHandler.handler(t, responseObserver);
    }
  }

  public StoreInfo convertStoreCreateReqToModel(StoreCreateReq request) {
    return new StoreInfo(null, request.getName(), request.getDescription(), null);
  }

  public StoreInfo convertStoreUpdateReqToModel(StoreUpdateReq request) {
    return new StoreInfo(
        UUID.fromString(request.getId()), request.getName(), request.getDescription(), null);
  }

  public StoreInfoRes convertStoreInfoToRes(StoreInfo storeInfo) {
    StoreInfoRes.Builder builder =
        StoreInfoRes.newBuilder()
            .setId(storeInfo.getId().toString())
            .setName(storeInfo.getName())
            .setDescription(storeInfo.getDescription());
    return builder.build();
  }

  public StoreListRes convertStoreInfoListResToRes(List<StoreInfo> storeInfoList) {
    // Make up store info response list
    StoreListRes.Builder builder = StoreListRes.newBuilder();
    Iterator<StoreInfo> iter = storeInfoList.iterator();
    while (iter.hasNext()) {
      builder.addStores(convertStoreInfoToRes(iter.next()));
    }
    return builder.build();
  }
}
