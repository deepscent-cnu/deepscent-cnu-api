package deepscent_cnu.deepscent_cnu_api.training_log.repository;

import deepscent_cnu.deepscent_cnu_api.training_log.entity.NormalOlfactoryTrainingRoundLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalOlfactoryTrainingRoundLogRepository extends
    JpaRepository<NormalOlfactoryTrainingRoundLog, Long> {

}
