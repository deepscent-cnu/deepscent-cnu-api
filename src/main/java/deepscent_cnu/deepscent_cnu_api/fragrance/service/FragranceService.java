package deepscent_cnu.deepscent_cnu_api.fragrance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.DeviceInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.SlotInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.repository.DeviceRegisterRepository;
import deepscent_cnu.deepscent_cnu_api.device_info.repository.SlotInfoRepository;
import deepscent_cnu.deepscent_cnu_api.exception.SlotMappingException;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.CorrectOptionRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CapsuleInfo;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CapsuleInfoResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CorrectScent;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CorrectScentListResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.ScentOptionsResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FragranceService {

  private static final int CARTRIDGE_COUNT = 4;
  private static final String AUTHORIZATION = "Authorization";
  private static final String DEEPSCENT_BASE_URL = "https://b2b-prod.deepscent.io";
  private final RestClient restClient = RestClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Value("${deepscent.access-token}")
  private String deepscentAccessToken;
  private DeviceRegisterRepository deviceRegisterRepository;
  private SlotInfoRepository slotInfoRepository;

  public FragranceService(DeviceRegisterRepository deviceRegisterRepository,
      SlotInfoRepository slotInfoRepository) {
    this.deviceRegisterRepository = deviceRegisterRepository;
    this.slotInfoRepository = slotInfoRepository;
  }

  public CapsuleInfoResponse getCartridgeState(@AuthToken Member member) throws Exception {
    List<CapsuleInfo> capsuleInfoList = new ArrayList<>();
    List<SlotInfo> slotInfoList = slotInfoRepository.findAllByMember(member);

    if (slotInfoList.size() < 12) {
      throw new SlotMappingException("향기 매핑을 먼저 완료해주세요.");
    }

    for (SlotInfo slotInfo : slotInfoList) {
      Integer deviceNumber = slotInfo.getDeviceNumber();
      Integer fanNumber = slotInfo.getFanNumber();
      String scent = slotInfo.getScent();

      capsuleInfoList.add(new CapsuleInfo(scent, deviceNumber, fanNumber));
    }

    return new CapsuleInfoResponse(capsuleInfoList);
  }

  public CorrectScentListResponse getCorrectScentList(@AuthToken Member member) throws Exception {
    List<SlotInfo> slotInfoList = slotInfoRepository.findAllByMember(member);
    List<CorrectScent> candidates = new ArrayList<>();

    if (slotInfoList.size() < 12) {
      throw new SlotMappingException("향기 매핑을 먼저 완료해주세요.");
    }

    for (SlotInfo slotInfo : slotInfoList) {
      Integer deviceNumber = slotInfo.getDeviceNumber();
      Integer fanNumber = slotInfo.getFanNumber();
      String scent = slotInfo.getScent();

      candidates.add(new CorrectScent(scent, deviceNumber, fanNumber));
    }

    Set<Integer> selectedIndexes = new HashSet<>();
    Random random = new Random();

    while (selectedIndexes.size() < 4) {
      int index = random.nextInt(candidates.size());
      selectedIndexes.add(index);
    }

    List<CorrectScent> correctScentList = new ArrayList<>();
    for (int idx : selectedIndexes) {
      correctScentList.add(candidates.get(idx));
    }

    return new CorrectScentListResponse(correctScentList);
  }

  public ScentOptionsResponse getScentOptions(CorrectOptionRequest correctOptionRequest)
      throws Exception {
    String scentOptionTrue = correctOptionRequest.correctOption();

    List<String> scentOptions = getScentOptionsFalse(scentOptionTrue);
    scentOptions.add(new Random().nextInt(4), scentOptionTrue);

    return new ScentOptionsResponse(
        scentOptionTrue,
        scentOptions.get(0),
        scentOptions.get(1),
        scentOptions.get(2),
        scentOptions.get(3)
    );
  }

  public void patchDeviceState(Member member, FanStateRequest fanStateRequest) {
    List<String> deviceIds = getDeviceIds(member);
    String targetDeviceId = deviceIds.get(fanStateRequest.deviceNumber() - 1);

    String target_uri = DEEPSCENT_BASE_URL + "/api/device/" + targetDeviceId + "/state";
    Map<String, Object> payload = Map.of("fan" + fanStateRequest.fanNumber(),
        fanStateRequest.fanSpeed(), "turbo", true);

    ResponseEntity<String> response = restClient.patch()
        .uri(URI.create(target_uri))
        .header(AUTHORIZATION, deepscentAccessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .retrieve()
        .toEntity(String.class);
  }

  private List<String> getCapsuleNames(List<String> capsuleSerials) throws JsonProcessingException {
    String target_uri = DEEPSCENT_BASE_URL + "/api/capsule/info?serials=" + String.join("&serials=",
        capsuleSerials);
    ResponseEntity<String> response = restClient.get()
        .uri(URI.create(target_uri))
        .header(AUTHORIZATION, deepscentAccessToken)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class);

    JsonNode rootNode = objectMapper.readTree(response.getBody());
    JsonNode capsuleNamesNode = rootNode.path("capsules");

    List<String> capsuleNames = new ArrayList<>();
    if (capsuleNamesNode.isArray()) {
      for (JsonNode capsuleNameNode : capsuleNamesNode) {
        capsuleNames.add(capsuleNameNode.path("name").path("ko").asText());
      }
    }

    return capsuleNames;
  }

  private List<String> getCapsuleSerials(String deviceId) throws JsonProcessingException {
    String target_uri = DEEPSCENT_BASE_URL + "/api/device/" + deviceId + "/state";
    ResponseEntity<String> response = restClient.get()
        .uri(URI.create(target_uri))
        .header(AUTHORIZATION, deepscentAccessToken)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class);

    JsonNode rootNode = objectMapper.readTree(response.getBody());
    JsonNode reportedNode = rootNode.path("reported");

    List<String> capsuleSerials = new ArrayList<>();
    for (int cartridgeNumber = 1; cartridgeNumber <= CARTRIDGE_COUNT; cartridgeNumber++) {
      capsuleSerials.add(reportedNode.path("cart" + cartridgeNumber).path("serial").asText());
    }

    return capsuleSerials;
  }

  private List<String> getDeviceIds(Member member) {
    DeviceInfo deviceInfo = deviceRegisterRepository.findByMember(member)
        .orElseThrow(() -> new NoSuchElementException("유저 상태가 올바르지 않습니다."));

    return List.of(deviceInfo.getDeviceId1(), deviceInfo.getDeviceId2(),
        deviceInfo.getDeviceId3());
  }

  private List<String> getScentOptionsFalse(String correctScentOption) {
    List<String> scentOptionCandidates = Arrays.asList("백미밥", "참기름", "장미", "된장", "나프탈렌", "치약", "레몬",
        "베르가못", "라벤더", "연탄", "허브", "청국장", "인센스", "파인", "고추장", "커피", "잡곡밥", "체리", "오렌지", "로즈마리",
        "버베나", "샌달우드", "네롤리", "라일락", "베티버", "앰버", "머스크");
    List<String> scentOptionCandidatesFiltered = new ArrayList<>();

    for (String scentOptionCandidate : scentOptionCandidates) {
      if (!scentOptionCandidate.equals(correctScentOption)) {
        scentOptionCandidatesFiltered.add(scentOptionCandidate);
      }
    }

    Set<Integer> selectedIndexes = new HashSet<>();
    Random random = new Random();
    while (selectedIndexes.size() < 3) {
      int index = random.nextInt(scentOptionCandidatesFiltered.size());
      selectedIndexes.add(index);
    }

    List<String> result = new ArrayList<>();
    for (int idx : selectedIndexes) {
      result.add(scentOptionCandidatesFiltered.get(idx));
    }

    return result;
  }
}
