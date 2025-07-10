package deepscent_cnu.deepscent_cnu_api.training_log.repository;

import deepscent_cnu.deepscent_cnu_api.training_log.entity.NormalOlfactoryTrainingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalOlfactoryTrainingLogRepository extends
    JpaRepository<NormalOlfactoryTrainingLog, Long> {

}
