package deepscent_cnu.deepscent_cnu_api.fragrance.controller;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.CorrectOptionRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CapsuleInfoResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CorrectScentListResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.ScentOptionsResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.service.FragranceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device/fragrance")
public class FragranceController {

  private final FragranceService fragranceService;

  public FragranceController(FragranceService fragranceService) {
    this.fragranceService = fragranceService;
  }

  @GetMapping("/capsule-info")
  public ResponseEntity<CapsuleInfoResponse> getCartridgeState(@AuthToken Member member)
      throws Exception {
    return ResponseEntity.ok().body(fragranceService.getCartridgeState(member));
  }

  @PostMapping("/scent-option")
  public ResponseEntity<ScentOptionsResponse> getScentOptions(
      @RequestBody CorrectOptionRequest correctOptionRequest)
      throws Exception {
    return ResponseEntity.ok().body(fragranceService.getScentOptions(correctOptionRequest));
  }

  @PostMapping("/fan-state")
  public ResponseEntity<Void> controlFanState(@AuthToken Member member,
      @Valid @RequestBody FanStateRequest fanStateRequest) {
    fragranceService.patchDeviceState(member, fanStateRequest);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/correct-list")
  public ResponseEntity<CorrectScentListResponse> getCorrectScentList(@AuthToken Member member)
      throws Exception {
    return ResponseEntity.ok().body(fragranceService.getCorrectScentList(member));
  }
}
