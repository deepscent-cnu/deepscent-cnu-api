package deepscent_cnu.deepscent_cnu_api.whisper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WhisperService {

  private final WhisperProperties whisperProperties;

  public String transcribeAudio(MultipartFile file) throws IOException {
    HttpPost post = new HttpPost(whisperProperties.getWhisperUrl());
    post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + whisperProperties.getApiKey());

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
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json).get("text").asText();
      }
    }
    return "STT 실패";
  }
}
