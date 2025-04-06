package deepscent_cnu.deepscent_cnu_api.fragrance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.external.deepscent.DeviceStateResponse;
import deepscent_cnu.deepscent_cnu_api.fragrance.dto.request.FanStateRequest;
import java.net.URI;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FragranceService {

  private final String AUTHORIZATION = "Authorization";

  private final String DEEPSCENT_BASE_URL = "https://b2b-prod.deepscent.io";
  private final RestClient restClient = RestClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public DeviceStateResponse getDeviceState(String deviceId) throws JsonProcessingException {
    String target_uri = DEEPSCENT_BASE_URL + "/api/device/" + deviceId + "/state";
    ResponseEntity<String> response = restClient.get()
        .uri(URI.create(target_uri))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class);

    JsonNode rootNode = objectMapper.readTree(response.getBody());
    JsonNode desiredNode = rootNode.path("desired");

    return objectMapper.treeToValue(desiredNode,
        DeviceStateResponse.class);
  }

  public void patchDeviceState(String deviceId, FanStateRequest fanStateRequest,
      String deepscentToken) {
    String target_uri = DEEPSCENT_BASE_URL + "/api/device/" + deviceId + "/state";
    Map<String, Object> payload = Map.of("fan" + fanStateRequest.fanNumber(),
        fanStateRequest.fanSpeed());

    ResponseEntity<String> response = restClient.patch()
        .uri(URI.create(target_uri))
        .header(AUTHORIZATION, deepscentToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .retrieve()
        .toEntity(String.class);
  }
}
