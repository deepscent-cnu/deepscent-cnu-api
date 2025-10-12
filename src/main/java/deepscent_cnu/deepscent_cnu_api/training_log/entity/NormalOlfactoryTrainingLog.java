package deepscent_cnu.deepscent_cnu_api.training_log.entity;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class NormalOlfactoryTrainingLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Member member;

  @Column(nullable = false)
  @CreationTimestamp
  private LocalDateTime trainingDate;

  @Column(nullable = false)
  private Long totalTimeTaken;

  public NormalOlfactoryTrainingLog(Member member,
      Long totalTimeTaken) {
    this.member = member;
    this.totalTimeTaken = totalTimeTaken;
  }
}
