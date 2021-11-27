package com.ssup2ket.store.domain.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
  private UUID id;
  private String loginId;
  private String role;
}
