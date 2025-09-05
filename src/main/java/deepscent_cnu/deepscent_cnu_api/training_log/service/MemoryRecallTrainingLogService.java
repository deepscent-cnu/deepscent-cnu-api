package deepscent_cnu.deepscent_cnu_api.training_log.service;

import deepscent_cnu.deepscent_cnu_api.openai.MemoryRecallRound;
import deepscent_cnu.deepscent_cnu_api.training_log.dto.request.MemoryRecallFeelingRequest;
import deepscent_cnu.deepscent_cnu_api.training_log.repository.MemoryRecallTrainingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoryRecallTrainingLogService {

  private final MemoryRecallTrainingLogRepository repository;

  public void saveFeeling(Long roundId, MemoryRecallFeelingRequest request) {
    MemoryRecallRound round = repository.findById(roundId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 roundId: " + roundId));

    round.setFeeling(request.feeling());
    repository.save(round);
  }
}
