package deepscent_cnu.deepscent_cnu_api.device_info.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.request.DeviceRegisterRequest;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.response.DeviceInfoResponse;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.DeviceInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.repository.DeviceRegisterRepository;
import java.net.URI;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class DeviceRegisterService {

  private static final String DEEPSCENT_BASE_URL = "https://b2b-prod.deepscent.io";
  private static final String AUTHORIZATION = "Authorization";
  private final RestClient restClient = RestClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final DeviceRegisterRepository deviceRegisterRepository;
  @Value("${deepscent.access-token}")
  private String deepscentAccessToken;

  public DeviceRegisterService(DeviceRegisterRepository deviceRegisterRepository) {
    this.deviceRegisterRepository = deviceRegisterRepository;
  }

  public void registerDeviceId(Member member, DeviceRegisterRequest deviceRegisterRequest) {
    DeviceInfo deviceInfo = deviceRegisterRepository.findByMember(member)
        .orElseThrow(() -> new NoSuchElementException("유저 상태가 올바르지 않습니다."));

    try {
      String targetUri = DEEPSCENT_BASE_URL + "/api/device/" + deviceRegisterRequest.deviceId();
      restClient.get()
          .uri(URI.create(targetUri))
          .header(AUTHORIZATION, deepscentAccessToken)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .toEntity(String.class);
    } catch (HttpClientErrorException e) {
      throw new IllegalArgumentException("유효하지 않은 기기 ID 입니다.");
    }

    DeviceInfo deviceInfoRegistered = deviceInfo.getRegistered(deviceRegisterRequest.deviceNumber(),
        deviceRegisterRequest.deviceId());

    deviceRegisterRepository.save(deviceInfoRegistered);
  }

  public DeviceInfoResponse getDeviceIdAll(Member member) {
    DeviceInfo deviceInfo = deviceRegisterRepository.findByMember(member)
        .orElseThrow(() -> new NoSuchElementException("유저 상태가 올바르지 않습니다."));

    return new DeviceInfoResponse(deviceInfo.getDeviceId1(), deviceInfo.getDeviceId2(),
        deviceInfo.getDeviceId3());
  }
}
