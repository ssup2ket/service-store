package com.ssup2ket.store.domain.model;

import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_removed")
public class UserInfoRemoved {
  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Length(max = 20)
  private String loginId;

  @Length(max = 10)
  private String role;
}
