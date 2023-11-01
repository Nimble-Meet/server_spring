package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.modules.meet.Meet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

  @Id
  @GeneratedValue
  private Long id;

  @Column
  @NotNull
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @NotNull
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToOne
  @NotNull
  private Meet meet;
}
