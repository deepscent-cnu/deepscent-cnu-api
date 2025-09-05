package deepscent_cnu.deepscent_cnu_api.training_log.repository;

import deepscent_cnu.deepscent_cnu_api.openai.MemoryRecallRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRecallTrainingLogRepository extends JpaRepository<MemoryRecallRound, Long> {

}
