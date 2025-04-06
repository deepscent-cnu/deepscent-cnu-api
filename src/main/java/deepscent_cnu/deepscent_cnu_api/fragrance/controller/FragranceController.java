package deepscent_cnu.deepscent_cnu_api.fragrance.controller;

import deepscent_cnu.deepscent_cnu_api.config.resolver.DeepscentToken;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.service.FragranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device/{deviceId}/fragrance")
public class FragranceController {

  private final FragranceService fragranceService;

  public FragranceController(FragranceService fragranceService) {
    this.fragranceService = fragranceService;
  }

  @PostMapping("/fan-state")
  public ResponseEntity<Void> controlFanState(@PathVariable("deviceId") String deviceId,
      @RequestBody FanStateRequest fanStateRequest, @DeepscentToken String deepscentToken) {
    fragranceService.patchDeviceState(deviceId, fanStateRequest, deepscentToken);
    return ResponseEntity.noContent().build();
  }
}
