package deepscent_cnu.deepscent_cnu_api.whisper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WhisperService {

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.whisper-url}")
  private String whisperUrl;

  public String transcribeAudio(MultipartFile file) throws IOException {
    HttpPost post = new HttpPost(whisperUrl);
    post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody("file", file.getBytes(), ContentType.DEFAULT_BINARY, file.getOriginalFilename());
    builder.addTextBody("model", "whisper-1");
    builder.addTextBody("language", "ko");

    post.setEntity(builder.build());

    try (CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post)) {

      HttpEntity entity = response.getEntity();
      if (entity != null) {
        String json = EntityUtils.toString(entity);
        System.out.println("🧾 Whisper 응답: " + json);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class);
        if (map.containsKey("text")) {
          return map.get("text").toString();
        } else {
          throw new IllegalStateException("응답에 'text' 필드가 없습니다.");
        }
      }
    }

    throw new IllegalStateException("Whisper 응답이 비어 있거나 처리에 실패했습니다.");
  }
}
