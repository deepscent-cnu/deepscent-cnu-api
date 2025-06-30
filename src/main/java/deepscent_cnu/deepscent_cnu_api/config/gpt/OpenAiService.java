package deepscent_cnu.deepscent_cnu_api.config.gpt;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepscent_cnu.deepscent_cnu_api.config.gpt.dto.Message;
import deepscent_cnu.deepscent_cnu_api.config.gpt.dto.RequestToAi;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class OpenAiService {

  private final String chatGptUrl = "https://api.openai.com/v1/chat/completions";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  @Value("${openai.api-key}")
  private String gptKey;

  public OpenAiService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
    this.restTemplate = restTemplateBuilder.build();
    this.objectMapper = objectMapper;
  }

  public String getQuestion(String answer) throws JsonProcessingException {
     return getResponseFromAi(answer + " 이건 사용자의 답변인데 이 답변과 자연스럽게 대화가 이어지도록"
         + "질문을 만들어줘 ");
  }

  public String getReport(String content) throws JsonProcessingException {
    return getResponseFromAi(content + " 이건 ai와 사용자간의 대화인데 이 대화내용을 기분을 중심으로"
        + "요약해줘");
  }

  public String getResponseFromAi(String content) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ResponseEntity<Object> feedBack = sandMessageToAi(content);
    String responseBody = objectMapper.writeValueAsString(feedBack.getBody());

    JsonNode rootNode = objectMapper.readTree(responseBody);
    String extractedContent = rootNode.path("choices").get(0).path("message").path("content")
        .asText();

    return extractedContent;
  }

  private ResponseEntity<Object> sandMessageToAi(String receivedMessages)
      throws JsonProcessingException {
    Message message = new Message("user", receivedMessages);
    List<Message> messageList = new LinkedList<>();
    messageList.add(message);

    RequestToAi diaryRequestToGpt = new RequestToAi("gpt-4o-mini", messageList);

    String jsonBody = objectMapper.writeValueAsString(diaryRequestToGpt);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + gptKey);

    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
    return restTemplate.exchange(chatGptUrl, HttpMethod.POST, requestEntity, Object.class);
  }

}
