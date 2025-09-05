package deepscent_cnu.deepscent_cnu_api.device_info.controller;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.request.DeviceRegisterRequest;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.response.DeviceInfoResponse;
import deepscent_cnu.deepscent_cnu_api.device_info.service.DeviceRegisterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
public class DeviceRegisterController {

  private final DeviceRegisterService deviceRegisterService;

  public DeviceRegisterController(DeviceRegisterService deviceRegisterService) {
    this.deviceRegisterService = deviceRegisterService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> registerDeviceId(@AuthToken Member member,
      @Valid @RequestBody DeviceRegisterRequest deviceRegisterRequest) {
    deviceRegisterService.registerDeviceId(member, deviceRegisterRequest);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<DeviceInfoResponse> getDeviceIdAll(@AuthToken Member member) {
    return ResponseEntity.ok().body(deviceRegisterService.getDeviceIdAll(member));
  }
}
