package com.ssup2ket.store.domain.model;

import com.ssup2ket.store.pkg.model.BaseCreatedModel;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inboxes")
public class Inbox extends BaseCreatedModel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Length(max = 255)
  private String aggregateType;

  @Length(max = 255)
  private String type;

  @Length(max = 255)
  private String payload;
}
