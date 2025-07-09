package deepscent_cnu.deepscent_cnu_api.training_log.controller;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.training_log.dto.request.NormalOlfactoryTrainingLogRequest;
import deepscent_cnu.deepscent_cnu_api.training_log.service.NormalOlfactoryTrainingLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/normal-olfactory-training/log")
public class NormalOlfactoryTrainingLogController {

  private final NormalOlfactoryTrainingLogService trainingLogService;

  public NormalOlfactoryTrainingLogController(
      NormalOlfactoryTrainingLogService trainingLogService) {
    this.trainingLogService = trainingLogService;
  }

  @PostMapping
  public ResponseEntity<Void> createNormalOlfactoryTrainingLog(@AuthToken Member member,
      @RequestBody
      NormalOlfactoryTrainingLogRequest trainingLogRequest) {
    trainingLogService.createTrainingLog(member, trainingLogRequest);
    return ResponseEntity.noContent().build();
  }
}
