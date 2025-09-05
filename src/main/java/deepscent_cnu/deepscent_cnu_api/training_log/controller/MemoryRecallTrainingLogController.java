package deepscent_cnu.deepscent_cnu_api.training_log.controller;

import deepscent_cnu.deepscent_cnu_api.training_log.dto.request.MemoryRecallFeelingRequest;
import deepscent_cnu.deepscent_cnu_api.training_log.service.MemoryRecallTrainingLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memory-recall-training/log")
@RequiredArgsConstructor
public class MemoryRecallTrainingLogController {

  private final MemoryRecallTrainingLogService service;

  @PostMapping("/{roundId}/feeling")
  public ResponseEntity<Void> saveFeeling(@PathVariable Long roundId,
      @RequestBody MemoryRecallFeelingRequest request) {
    service.saveFeeling(roundId, request);
    return ResponseEntity.noContent().build();
  }
}
