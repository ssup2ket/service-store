package com.ssup2ket.store.domain.model;

import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.Min;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Length(max = 30)
  private String name;

  @Min(0)
  private int quantity;

  @Length(max = 50)
  private String description;

  @Column(columnDefinition = "BINARY(16)")
  private UUID inventoryId;
}
