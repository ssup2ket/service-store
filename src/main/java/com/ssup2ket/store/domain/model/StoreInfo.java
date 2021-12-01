package com.ssup2ket.store.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssup2ket.store.pkg.model.BaseCreatedUpdatedModel;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
public class StoreInfo extends BaseCreatedUpdatedModel {
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

  public String toJsonString() throws JsonProcessingException {
    ObjectMapper jsonMapper = new ObjectMapper();
    return jsonMapper.writeValueAsString(this);
  }
}
