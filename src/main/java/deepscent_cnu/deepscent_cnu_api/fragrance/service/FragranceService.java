package deepscent_cnu.deepscent_cnu_api.fragrance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.CapsuleNamesResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.response.ScentOptionsResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  private static List<String> getScentOptionsFalse(String correctScentOption) {
    List<String> scentOptionCandidates = Arrays.asList("백미밥", "참기름", "장미", "된장", "나프탈렌", "치약", "레몬",
        "베르가못", "라벤더", "연탄", "허브", "청국장");
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

  public CapsuleNamesResponse getCartridgeState(String deviceId) throws Exception {
    List<String> capsuleSerials = getCapsuleSerials(deviceId);
    List<String> capsuleNames = getCapsuleNames(capsuleSerials);

    return new CapsuleNamesResponse(
        capsuleNames.get(0),
        capsuleNames.get(1),
        capsuleNames.get(2),
        capsuleNames.get(3)
    );
  }

  public ScentOptionsResponse getScentOptions(String deviceId, int round) throws Exception {
    List<String> capsuleSerials = getCapsuleSerials(deviceId);
    List<String> capsuleNames = getCapsuleNames(capsuleSerials);
    String scentOptionTrue = capsuleNames.get(round - 1);

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

  public void patchDeviceState(String deviceId, FanStateRequest fanStateRequest) {
    String target_uri = DEEPSCENT_BASE_URL + "/api/device/" + deviceId + "/state";
    Map<String, Object> payload = Map.of("fan" + fanStateRequest.fanNumber(),
        fanStateRequest.fanSpeed());

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
}
