package deepscent_cnu.deepscent_cnu_api.openai;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
public class MemoryRecallRound {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Member member;

  @Column(nullable = false)
  private Long round;

  @Column(nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private String scent;

  @Column(columnDefinition = "TEXT")
  private String summary;

  @Column(columnDefinition = "TEXT")
  private String feeling;

  @OneToMany(
      mappedBy = "memoryRecallRound",
      cascade = CascadeType.ALL,
      orphanRemoval = true,fetch = FetchType.LAZY,mappedBy = "memoryRecallRound")
  @JsonManagedReference("round-messages")
  private List<UserChatMemory> messages = new ArrayList<>();

}
