package deepscent_cnu.deepscent_cnu_api.training_log.service;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.training_log.dto.request.NormalOlfactoryTrainingLogRequest;
import deepscent_cnu.deepscent_cnu_api.training_log.dto.request.NormalOlfactoryTrainingLogRequest.RoundLog;
import deepscent_cnu.deepscent_cnu_api.training_log.entity.NormalOlfactoryTrainingLog;
import deepscent_cnu.deepscent_cnu_api.training_log.entity.NormalOlfactoryTrainingRoundLog;
import deepscent_cnu.deepscent_cnu_api.training_log.repository.NormalOlfactoryTrainingLogRepository;
import deepscent_cnu.deepscent_cnu_api.training_log.repository.NormalOlfactoryTrainingRoundLogRepository;
import org.springframework.stereotype.Service;

@Service
public class NormalOlfactoryTrainingLogService {

  private final NormalOlfactoryTrainingLogRepository trainingLogRepository;
  private final NormalOlfactoryTrainingRoundLogRepository roundLogRepository;

  public NormalOlfactoryTrainingLogService(
      NormalOlfactoryTrainingLogRepository trainingLogRepository,
      NormalOlfactoryTrainingRoundLogRepository roundLogRepository) {
    this.trainingLogRepository = trainingLogRepository;
    this.roundLogRepository = roundLogRepository;
  }

  public void createTrainingLog(Member member, NormalOlfactoryTrainingLogRequest logRequest) {
    NormalOlfactoryTrainingLog trainingLog = new NormalOlfactoryTrainingLog(member,
        logRequest.totalTimeTaken());
    NormalOlfactoryTrainingLog createdTrainingLog = trainingLogRepository.save(trainingLog);

    for (int round = 1; round <= 4; round++) {
      RoundLog roundLogRequest = logRequest.roundLogs().get(round - 1);
      NormalOlfactoryTrainingRoundLog roundLog = new NormalOlfactoryTrainingRoundLog(
          createdTrainingLog, round, roundLogRequest.correctOption(),
          roundLogRequest.selectedOption(), roundLogRequest.isCorrect(),
          roundLogRequest.timeTaken());

      roundLogRepository.save(roundLog);
    }
  }
}
