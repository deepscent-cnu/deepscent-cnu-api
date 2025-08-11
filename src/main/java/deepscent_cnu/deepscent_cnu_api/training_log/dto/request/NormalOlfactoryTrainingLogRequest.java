package deepscent_cnu.deepscent_cnu_api.training_log.dto.request;

import java.util.List;

public record NormalOlfactoryTrainingLogRequest(
    Long totalTimeTaken,
    List<RoundLog> roundLogs

) {

  public record RoundLog(
      String correctOption,
      String selectedOption,
      Boolean isCorrect,
      Long timeTaken,
      Integer scentStrength
  ) {

  }

}

