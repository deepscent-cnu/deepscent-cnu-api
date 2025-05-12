package deepscent_cnu.deepscent_cnu_api.fragrance.controller;

import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CapsuleNamesResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.service.FragranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device/{deviceId}/fragrance")
public class FragranceController {

  private final FragranceService fragranceService;

  public FragranceController(FragranceService fragranceService) {
    this.fragranceService = fragranceService;
  }

  @GetMapping("/capsule-info")
  public ResponseEntity<CapsuleNamesResponse> getCartridgeState(
      @PathVariable("deviceId") String deviceId)
      throws Exception {
    return ResponseEntity.ok().body(fragranceService.getCartridgeState(deviceId));
  }

  @PostMapping("/fan-state")
  public ResponseEntity<Void> controlFanState(@PathVariable("deviceId") String deviceId,
      @RequestBody FanStateRequest fanStateRequest) {
    fragranceService.patchDeviceState(deviceId, fanStateRequest);
    return ResponseEntity.noContent().build();
  }
}
