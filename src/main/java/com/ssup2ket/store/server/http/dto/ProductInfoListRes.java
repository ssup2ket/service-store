package com.ssup2ket.store.server.http.dto;

import com.ssup2ket.store.domain.model.ProductInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoListRes {
   List<ProductInfo> inventories;
}
