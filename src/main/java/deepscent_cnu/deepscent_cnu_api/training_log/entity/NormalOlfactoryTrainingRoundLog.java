package deepscent_cnu.deepscent_cnu_api.training_log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class NormalOlfactoryTrainingRoundLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private NormalOlfactoryTrainingLog trainingLog;

  @Column(nullable = false)
  private Integer round;

  @Column(nullable = false)
  private String correctOption;

  @Column(nullable = false)
  private String selectedOption;

  @Column(nullable = false)
  private Boolean isCorrect;

  @Column(nullable = false)
  private Long timeTaken;

  @Column(nullable = false)
  private Integer scentStrength;

  public NormalOlfactoryTrainingRoundLog(NormalOlfactoryTrainingLog trainingLog, Integer round,
      String correctOption, String selectedOption, Boolean isCorrect, Long timeTaken, Integer scentStrength) {
    this.trainingLog = trainingLog;
    this.round = round;
    this.correctOption = correctOption;
    this.selectedOption = selectedOption;
    this.isCorrect = isCorrect;
    this.timeTaken = timeTaken;
    this.scentStrength = scentStrength;
  }
}
