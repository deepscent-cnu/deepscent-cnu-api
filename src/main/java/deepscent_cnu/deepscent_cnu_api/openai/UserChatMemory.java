package deepscent_cnu.deepscent_cnu_api.openai;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_chat_memory")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChatMemory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long memoryId; // 사용자 구분 값

  @ManyToOne
  private MemoryRecallRound memoryRecallRound; // 회차 구분 값

  private String role; // user / assistant
  @Column(length = 2000)
  private String message;

  private LocalDateTime createdAt;
}
