package com.ssup2ket.store.domain.model;

import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
public class StoreInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Length(max = 30)
  private String name;

  @Length(max = 50)
  private String description;

  @Column(columnDefinition = "BINARY(16)")
  private UUID userId;
}
