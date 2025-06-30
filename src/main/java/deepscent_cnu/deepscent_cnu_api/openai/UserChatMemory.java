package deepscent_cnu.deepscent_cnu_api.openai;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private Integer memoryId; // 사용자 구분 값

    private String role; // user / assistant
    @Column(length = 2000)
    private String message;

    private LocalDateTime createdAt;
}
